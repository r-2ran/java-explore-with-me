package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.AccessDeniedException;
import ru.practicum.exception.ConflictRequestParamException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.state.*;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.security.AccessControlException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.event.mapper.EventMapper.*;
import static ru.practicum.location.mapper.LocationMapper.*;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient client;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;


    @Override
    public EventFullDto addEventUser(Long userId, NewEventDto eventDto) {
        if (eventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new AccessDeniedException("event cannot be in the past");
        }
        Event event = Event.builder()
                .annotation(eventDto.getAnnotation())
                .description(eventDto.getDescription())
                .title(eventDto.getTitle())
                .initiator(userRepository.findById(userId)
                        .orElseThrow(() -> new NotFoundException(
                                String.format("user id = %d not found", userId))))
                .category(categoryRepository.findById(eventDto.getCategory())
                        .orElseThrow(() -> new NotFoundException(
                                String.format("category id = %d not found", eventDto.getCategory()))))
                .location(locationRepository.findByLonAndLat(
                        eventDto.getLocation().getLon(), eventDto.getLocation().getLat())
                        .orElse(setLocation(eventDto.getLocation())))
                .created(LocalDateTime.now())
                .eventDate(eventDto.getEventDate())
                .views(0)
                .state(State.PENDING)
                .build();
        if (eventDto.getRequestModeration() == null) {
            event.setRequestModeration(true);
        } else {
            event.setRequestModeration(eventDto.getRequestModeration());
        }
        if (eventDto.getPaid() == null) {
            event.setPaid(false);
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
    public List<EventShortDto> getEventByUserId(Long userId, int from, int size) {
        from = from / size;
        Pageable pageable = PageRequest.of(from, size);
        return toShortEventDtoList(eventRepository.findAllEventsByInitiatorId(userId, pageable));
    }

    @Override
    public List<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable, SortState sort, int from, int size,
                                               HttpServletRequest request) {
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
            switch (sort) {
                case EVENT_DATE:
                    events.sort(Comparator.comparing(Event::getEventDate));
                    break;
                case VIEWS:
                    events.sort(Comparator.comparing(Event::getViews));
                    break;
                default:
                    throw new ValidationException("UNSUPPORTED SORT");
            }
        }
        if (onlyAvailable) {
            addHit(request);
            return toShortEventDtoList(events.stream()
                    .filter(event -> (event.getParticipantLimit() > event.getConfirmedRequests().size())
                            || event.getParticipantLimit() == 0)
                    .collect(Collectors.toList()));
        }
        addHit(request);
        return toShortEventDtoList(events);
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<State> states, List<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               int from, int size) {
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
    public EventFullDto getEventByIdPublic(Long eventId, HttpServletRequest request) {
        Event event = checkEvent(eventId);
        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException(String.format("event %d not PUBLISHED", eventId));
        }
        Integer views = getViews(eventId);
        addHit(request);
        Integer updatedViews = getViews(eventId);
        if (views < updatedViews) {
            event.setViews(event.getViews() + 1);
        }
        return toFull(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("event id %d not exist", eventId)));
        return toFull(event);
    }

    @Override
    public EventFullDto updateEventUser(Long userId, Long eventId, UpdateEventUserRequest userRequest) {
        checkUser(userId);
        Event event = checkEvent(eventId);
        if (event.getState().equals(State.PUBLISHED)) {
            throw new AccessDeniedException("published cannot be updated");
        }
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new AccessDeniedException("cannot be in the past");
        }

        if (userRequest.getEventDate() != null) {
            if (userRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException("cannot be in future less than 2 h");
            }
            event.setEventDate(userRequest.getEventDate());
        }
        if (userRequest.getAnnotation() != null) {
            event.setAnnotation(userRequest.getAnnotation());
        }
        if (userRequest.getCategory() != null) {
            event.setCategory(categoryRepository.findById(userRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException(
                            String.format("category id %d not exist", userRequest.getCategory()))));
        }
        if (userRequest.getDescription() != null) {
            event.setDescription(userRequest.getDescription());
        }
        if (userRequest.getLocation() != null) {
            event.setLocation(locationRepository.findByLonAndLat(
                    userRequest.getLocation().getLon(), userRequest.getLocation().getLat())
                    .orElse(setLocation(userRequest.getLocation())));
        }
        if (userRequest.getPaid() != null) {
            event.setPaid(userRequest.getPaid());
        }
        if (userRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(userRequest.getParticipantLimit());
        }
        if (userRequest.getRequestModeration() != null) {
            event.setRequestModeration(userRequest.getRequestModeration());
        }
        if (userRequest.getStateAction() != null) {
            if (userRequest.getStateAction() == StateActionUser.SEND_TO_REVIEW) event.setState(State.PENDING);
            if (userRequest.getStateAction() == StateActionUser.CANCEL_REVIEW) event.setState(State.CANCELED);
        }
        if (userRequest.getTitle() != null) {
            event.setTitle(userRequest.getTitle());
        }
        return toFull(eventRepository.save(event));
    }

    @Override
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest eventAdminRequest) {
        Event event = checkEvent(eventId);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new AccessDeniedException("cannot update event in past");
        }

        if (eventAdminRequest.getEventDate() != null) {
            if (eventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new AccessControlException("event cannot be in past");
            }
            event.setEventDate(eventAdminRequest.getEventDate());
        }
        if (eventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(eventAdminRequest.getAnnotation());
        }
        if (eventAdminRequest.getDescription() != null) {
            event.setDescription(eventAdminRequest.getDescription());
        }
        if (eventAdminRequest.getCategory() != null) {
            event.setCategory(categoryRepository.findById(eventAdminRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException(
                            String.format("category id %d not exist", eventAdminRequest.getCategory()))));
        }
        if (eventAdminRequest.getLocation() != null) {
            event.setLocation(locationRepository.findByLonAndLat(
                    eventAdminRequest.getLocation().getLon(), eventAdminRequest.getLocation().getLat())
                    .orElse(setLocation(eventAdminRequest.getLocation())));
        }
        if (eventAdminRequest.getPaid() != null) {
            event.setPaid(eventAdminRequest.getPaid());
        }
        if (eventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(eventAdminRequest.getParticipantLimit());
        }
        if (eventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(eventAdminRequest.getRequestModeration());
        }
        if (eventAdminRequest.getStateAction() != null) {
            if (eventAdminRequest.getStateAction().equals(StateActionAdmin.PUBLISH_EVENT)) {
                if (event.getState() != State.PENDING) {
                    throw new AccessDeniedException("event state is not PENDING");
                }
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else {
                if (event.getState() == State.PUBLISHED) {
                    throw new ConflictRequestParamException("already published");
                }
                event.setState(State.CANCELED);
            }
        }
        if (eventAdminRequest.getTitle() != null) {
            event.setTitle(eventAdminRequest.getTitle());
        }
        return toFull(eventRepository.save(event));
    }

    @Override
    public EventRequestStatusUpdateResult confirmRequest(Long userId, Long eventId,
                                                         EventRequestStatusUpdateRequest updateRequest) {
        checkUser(userId);
        Event event = checkEvent(eventId);
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new AccessDeniedException("getParticipantLimit = 0 or not need to moderation");
        }

        List<Request> requests = requestRepository.findAllById(updateRequest.getRequestIds());
        int limit = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (limit == event.getParticipantLimit()) {
            throw new AccessDeniedException("participant limit reached");
        }
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult(
                new ArrayList<>(), new ArrayList<>());
        for (Request request : requests) {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new AccessDeniedException("must have status PENDING");
            }
            if (updateRequest.getStatus() == UpdateRequestStatus.CONFIRMED) {
                if (limit < event.getParticipantLimit()) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    result.getConfirmedRequests().add(RequestMapper.toDto(request));
                    limit++;
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                    result.getRejectedRequests().add(RequestMapper.toDto(request));
                }
            } else {
                request.setStatus(RequestStatus.REJECTED);
                result.getRejectedRequests().add(RequestMapper.toDto(request));
            }
        }
        requestRepository.saveAll(requests);
        return result;
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {
        checkEvent(eventId);
        return requestRepository.findAllByEventId(eventId).stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("no such user id = %d", userId))
                );
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("no such event id = %d", eventId))
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
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"),
                rangeStart);
    }

    private Specification<Event> eventDateIsLess(LocalDateTime rangeEnd) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get("eventDate"), rangeEnd);
    }

    private Specification<Event> paidIs(Boolean paid) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("paid"), paid);
    }

    private void addHit(HttpServletRequest request) {
        client.addEndpointHitDto(new EndpointHitDto(
                "ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()));
    }

    private Integer getViews(Long eventId) {
        ResponseEntity<ViewStatsDto[]> response = client.getViewStatsDto(LocalDateTime.now().minusYears(2),
                LocalDateTime.now(),
                new String[]{"/events/" + eventId},
                true);
        int views = 0;
        Optional<ViewStatsDto> stat;
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            stat = Arrays.stream(response.getBody()).findFirst();
            if (stat.isPresent()) {
                views = Math.toIntExact(stat.get().getHits());
            }
        }
        return views;
    }

    private Location setLocation(LocationDto locationDto) {
        return locationRepository.save(toLocation(locationDto));
    }
}
