package ru.practicum.event.service;

import org.springframework.stereotype.Service;
import ru.practicum.event.dto.*;
import ru.practicum.state.State;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
public interface EventService {

    EventFullDto addEvent(Long userId, NewEventDto eventDto);

    List<EventShortDto> getAllByUser(Long userId, Integer from, Integer size);

    EventFullDto getById(Long eventId, HttpServletRequest request);

    EventFullDto getByUserAndEvent(Long userId, Long eventId);

    List<EventShortDto> findAllEvents(String text, List<Long> categories, Boolean paid,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                      String sort, int from, int size, HttpServletRequest request);

    List<EventFullDto> findAllEventsAdmin(List<Long> users, List<State> states, List<Long> categories,
                                          LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventUserRequest);

    EventFullDto cancelEvent(Long userId, Long eventId);

    EventFullDto confirmEvent(Long eventId);

}
