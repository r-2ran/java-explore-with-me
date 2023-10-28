package ru.practicum.event.service;

import org.springframework.stereotype.Service;
import ru.practicum.event.dto.*;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.state.SortState;
import ru.practicum.state.State;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
public interface EventService {
    EventFullDto addEventUser(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getEventByUserId(Long userId, int from, int size);

    List<EventShortDto> getEventsPublic(String text, List<Long> categories,
                                        Boolean paid, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, Boolean onlyAvailable,
                                        SortState sort, int from, int size,
                                        HttpServletRequest request);

    List<EventFullDto> getEventsByAdmin(List<Long> users, List<State> states,
                                        List<Long> categories, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, int from, int size);

    EventFullDto getEventByIdPublic(Long eventId, HttpServletRequest request);


    EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId, HttpServletRequest request);

    EventFullDto updateEventUser(Long userId, Long eventId, UpdateEventUserRequest userRequest);


    EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest adminRequest);

    EventRequestStatusUpdateResult confirmRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest);

    List<ParticipationRequestDto> getRequests(Long eventId, Long userId);

}
