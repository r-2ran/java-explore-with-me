package ru.practicum.event.service;

import org.springframework.stereotype.Service;
import ru.practicum.event.dto.*;
import ru.practicum.state.State;

import java.util.List;

@Service
public interface EventService {

    EventFullDto addEvent(Long userId, NewEventDto eventDto);

    List<EventShortDto> getAllByUser(Long userId, Integer from, Integer size);

    EventFullDto getById(Long eventId);

    EventFullDto getByUserAndEvent(Long userId, Long eventId);

    List<EventShortDto> findAllEvents(String text, List<Long> categories, Boolean paid,
                                      String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                      String sort, int from, int size);

    List<EventFullDto> findAllEventsAdmin(List<Long> users, List<State> states, List<Long> categories,
                                          String rangeStart, String rangeEnd, int from, int size);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventUserRequest);

    EventFullDto cancelEvent(Long userId, Long eventId);

    EventFullDto rejectEvent(Long eventId);

    EventFullDto confirmEvent(Long eventId);

}
