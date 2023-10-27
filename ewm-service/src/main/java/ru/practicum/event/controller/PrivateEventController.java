package ru.practicum.event.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class PrivateEventController {
    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId,
                                 @Valid @RequestBody NewEventDto eventDto) {
        return eventService.addEventUser(userId, eventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getByUserAndEvent(@PathVariable Long userId,
                                          @PathVariable Long eventId,
                                          HttpServletRequest request) {
        return eventService.getEventByUserIdAndEventId(userId, eventId, request);
    }

    @GetMapping
    public List<EventShortDto> getAllByUser(@PathVariable Long userId,
                                            @PositiveOrZero
                                            @RequestParam(value = "from", defaultValue = "0",
                                                    required = false) Integer from,
                                            @Positive
                                            @RequestParam(value = "size", defaultValue = "10",
                                                    required = false) Integer size) {
        return eventService.getEventByUserId(userId, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto changeEventByUser(@PathVariable Long userId,
                                          @PathVariable Long eventId,
                                          @Valid @RequestBody UpdateEventUserRequest eventUserRequest) {
        return eventService.updateEventUser(userId, eventId, eventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getAllByUserAndEvent(@PathVariable Long userId,
                                                              @PathVariable Long eventId) {
        return requestService.getAllByUserAndEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequest(@PathVariable Long userId,
                                                        @PathVariable Long eventId,
                                                        @Valid @RequestBody
                                                        EventRequestStatusUpdateRequest request) {
        return requestService.updateRequest(request, eventId, userId);
    }
}
