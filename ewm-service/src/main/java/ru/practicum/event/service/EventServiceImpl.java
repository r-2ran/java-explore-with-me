package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.category.service.CategoryService;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.AccessDeniedException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.location.model.Location;
import ru.practicum.location.service.LocationService;
import ru.practicum.state.State;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.event.mapper.EventMapper.*;
import static ru.practicum.category.mapper.CategoryMapper.*;
import static ru.practicum.location.mapper.LocationMapper.*;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final LocationService locationService;


    @Override
    public EventFullDto addEvent(Long userId, NewEventDto eventDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("no user id %d", userId)));
        if (eventDto.getEventDate() != null &&
                LocalDateTime.parse(eventDto.getEventDate(), FORMATTER)
                        .isBefore(LocalDateTime.now().plusHours(5))) {
            throw new ValidationException("wrong event date, it cannot be in past");
        }
        Category category = toCategory(categoryService.getCategoryById(eventDto.getCategory()));
        Location location = locationService.addLocation(toLocationDto(eventDto.getLocation()));
        Event event = to(eventDto);
        event.setState(State.PENDING);
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
    public List<EventShortDto> getAllByUser(Long userId, Integer from, Integer size) {
        checkUser(userId);
        from = from / size;
        Pageable pageable = PageRequest.of(from, size);
        return toShortEventDtoList(eventRepository.findAllEventsByInitiatorId(userId, pageable));
    }

    @Override
    public EventFullDto getById(Long eventId) {
        return toFull(checkEvent(eventId));
    }

    @Override
    public EventFullDto getByUserAndEvent(Long userId, Long eventId) {
        checkUser(userId);
        checkEvent(eventId);
        return toFull(eventRepository.findByIdAndInitiatorId(eventId, userId));
    }

    @Override
    public List<EventShortDto> findAllEvents(String text, List<Long> categories, Boolean paid, String rangeStart,
                                             String rangeEnd, Boolean onlyAvailable, String sort, int from,
                                             int size) {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        from = from / size;
        Pageable pageable = PageRequest.of(from, size);
        List<Event> events;

        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, FORMATTER);
        }
        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, FORMATTER);
        }
        if (categories == null) {
            categories = new ArrayList<>();
        }
        if (text == null) {
            events = eventRepository.findEventsWithoutText(categories, paid, start, end, onlyAvailable, pageable);
        } else {
            events = eventRepository.findEvents(text, categories, paid, start, end, onlyAvailable, pageable);
        }

        if (Objects.equals(sort, "EVENT_DATE")) {
            events = events
                    .stream()
                    .sorted(Comparator.comparing(Event::getEventDate).reversed())
                    .collect(Collectors.toList());
        }
        return toShortEventDtoList(events);
    }

    @Override
    public List<EventFullDto> findAllEventsAdmin(List<Long> users, List<State> states, List<Long> categories,
                                                 String rangeStart, String rangeEnd, int from, int size) {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();

        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, FORMATTER);
        }

        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, FORMATTER);
        }

        if (users == null) {
            users = new ArrayList<>();
        }

        if (states == null) {
            states = new ArrayList<>();
            states.add(State.PENDING);
            states.add(State.CANCELED);
            states.add(State.PUBLISHED);
        }

        if (categories == null) {
            categories = new ArrayList<>();
        }
        from = from / size;
        Pageable pageable = PageRequest.of(from, size);
        List<Event> events = eventRepository.adminFindEvents(users, states, categories, start, end, pageable);
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
            event.setLat(updateEventAdminRequest.getLocation().getLat());
            event.setLon(updateEventAdminRequest.getLocation().getLon());
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
    public EventFullDto rejectEvent(Long eventId) {
        Event event = checkEvent(eventId);
        event.setState(State.CANCELED);
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
}
