package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsClient;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.category.service.CategoryService;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.AccessDeniedException;
import ru.practicum.exception.ConflictRequestParamException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.state.SortState;
import ru.practicum.state.State;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.event.mapper.EventMapper.*;
import static ru.practicum.category.mapper.CategoryMapper.*;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    private final StatsClient client;


    @Override
    public EventFullDto addEvent(Long userId, NewEventDto eventDto) {
        if (LocalDateTime.parse(eventDto.getEventDate(), FORMATTER).isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictRequestParamException("error date, event cannot be in past");
        }
        Event event = Event.builder()
                .annotation(eventDto.getAnnotation())
                .description(eventDto.getDescription())
                .title(eventDto.getTitle())
                .initiator(checkUser(userId))
                .category(toCategory(categoryService.getCategoryById(eventDto.getCategory())))
                .location(eventDto.getLocation())
                .created(LocalDateTime.now())
                .eventDate(LocalDateTime.parse(eventDto.getEventDate(), FORMATTER))
                .views(0)
                .state(State.PENDING)
                .build();
        if (eventDto.getRequestModeration() == null) {
            event.setRequestModeration(Boolean.TRUE);
        } else {
            event.setRequestModeration(eventDto.getRequestModeration());
        }
        if (eventDto.getPaid() == null) {
            event.setPaid(Boolean.FALSE);
        } else {
            event.setPaid(eventDto.getPaid());
        }
        if (eventDto.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        } else {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }
        return toFull(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getAllByUser(Long userId, Integer from, Integer size) {
        checkUser(userId);
        from = from / size;
        Pageable pageable = PageRequest.of(from, size);
        return toShortEventDtoList(eventRepository.findAllEventsByInitiatorId(userId, pageable));
    }

    @Override
    public EventFullDto getById(Long eventId, HttpServletRequest request) {
        Event event = checkEvent(eventId);
        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException(String.format("event id %d don't exist", eventId));
        }
        return toFull(eventRepository.save(event));
    }

    @Override
    public EventFullDto getByUserAndEvent(Long userId, Long eventId) {
        checkUser(userId);
        checkEvent(eventId);
        return toFull(eventRepository.findByIdAndInitiatorId(eventId, userId));
    }

    @Override
    public List<EventShortDto> findAllEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, int from,
                                             int size, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        checkDate(rangeStart, rangeEnd);
        List<Specification<Event>> specifications = new ArrayList<>();
        if (categories != null) {
            List<Category> categoryList = categories.stream()
                    .map(id -> categoryRepository.findById(id)
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            specifications.add(categoryIdIn(categoryList));
        }
        specifications.add(paid == null ? null : paidIs(paid));
        specifications.add(eventDateIsGreaterOrEqual(rangeStart));
        specifications.add(rangeEnd == null ? null : eventDateIsLess(rangeEnd));
        specifications.add(stateIn(List.of(State.PUBLISHED)));
        Specification<Event> specification = specifications.stream()
                .filter(Objects::nonNull)
                .reduce(Specification::and)
                .orElseThrow();
        List<Event> events;
        if (text == null) {
            events = eventRepository.findAll(specification, pageable).stream().collect(Collectors.toList());
        } else {
            events = new ArrayList<>(eventRepository.findAllByText(text, specification, pageable));
        }
        if (sort != null) {
            SortState state = SortState.valueOf(sort);
            switch (state) {
                case EVENT_DATE:
                    events.sort(Comparator.comparing(Event::getEventDate));
                    break;
                case VIEWS:
                    events.sort(Comparator.comparing(Event::getViews));
                    break;
                default:
                    throw new ValidationException("UNSUPPORTED SORT STATE");
            }
        }
        if (onlyAvailable) {
            addHit(request);
            return toShortEventDtoList(events);
        }
        addHit(request);
        return toShortEventDtoList(events);
    }

    @Override
    public List<EventFullDto> findAllEventsAdmin(List<Long> users, List<State> states, List<Long> categories,
                                                 LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        checkDate(rangeStart, rangeEnd);
        Pageable pageable = PageRequest.of(from / size, size);
        List<Specification<Event>> specifications = new ArrayList<>();
        if (users != null) {
            List<User> userList = users.stream()
                    .map(id -> userRepository.findById(id)
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            specifications.add(initiatorIdIn(userList));
        }
        if (categories != null) {
            List<Category> categoryList = categories.stream()
                    .map(id -> categoryRepository.findById(id)
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            specifications.add(categoryIdIn(categoryList));
        }
        specifications.add(states == null ? null : stateIn(states));
        specifications.add(rangeStart == null ? null : eventDateIsGreaterOrEqual(rangeStart));
        specifications.add(rangeEnd == null ? null : eventDateIsLess(rangeEnd));
        Specification<Event> specification = specifications.stream()
                .filter(Objects::nonNull)
                .reduce(Specification::and)
                .orElse(null);
        List<Event> events;
        if (specification != null) {
            events = eventRepository.findAll(specification, pageable).toList();
        } else {
            events = eventRepository.findAll(pageable).toList();
        }
        return toFullEventDtoList(events);

    }

    @Override
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest eventUserRequest) {
        checkUser(userId);
        Event event = checkEvent(eventId);
        if (event.getState() == State.CANCELED || event.getState() == State.UNSUPPORTED_STATE
                || event.getState() == State.PUBLISHED) {
            throw new AccessDeniedException("don't have access to update event");
        }

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new AccessDeniedException(String.format("user id %d don't have permission to update event",
                    userId));
        }

        if (eventUserRequest.getAnnotation() != null) {
            event.setAnnotation(eventUserRequest.getAnnotation());
        }
        if (eventUserRequest.getDescription() != null) {
            event.setDescription(eventUserRequest.getDescription());
        }
        if (eventUserRequest.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(eventUserRequest.getEventDate(), FORMATTER));
        }
        if (eventUserRequest.getPaid() != null) {
            event.setPaid(eventUserRequest.getPaid());
        }
        if (eventUserRequest.getTitle() != null) {
            event.setTitle(eventUserRequest.getTitle());
        }
        if (eventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUserRequest.getParticipantLimit());
        }
        return toFull(eventRepository.save(event));
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = checkEvent(eventId);
        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(updateEventAdminRequest.getLocation());
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }
        return toFull(eventRepository.save(event));
    }

    @Override
    public EventFullDto cancelEvent(Long userId, Long eventId) {
        checkEvent(eventId);
        checkUser(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        return toFull(eventRepository.save(event));
    }

    @Override
    public EventFullDto confirmEvent(Long eventId) {
        Event event = checkEvent(eventId);
        event.setState(State.PUBLISHED);
        return toFull(eventRepository.save(event));
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("no such user id = %d", userId))
                );
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("no such user id = %d", eventId))
                );
    }

    private void checkDate(LocalDateTime start, LocalDateTime end) {
        if (end != null && start.isAfter(end)) {
            throw new ValidationException("start date cannot be after end date");
        }
    }

    private Specification<Event> initiatorIdIn(List<User> users) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("initiator")).value(users);
    }

    private Specification<Event> stateIn(List<State> states) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("state")).value(states);
    }

    private Specification<Event> categoryIdIn(List<Category> categories) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("category")).value(categories);
    }

    private Specification<Event> eventDateIsGreaterOrEqual(LocalDateTime rangeStart) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart);
    }

    private Specification<Event> eventDateIsLess(LocalDateTime rangeEnd) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get("eventDate"), rangeEnd);
    }

    private Specification<Event> paidIs(Boolean paid) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("paid"), paid);
    }

    private void addHit(HttpServletRequest request) {
        client.addEndpointHit(new EndpointHitDto(
                "ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()));
    }
}
