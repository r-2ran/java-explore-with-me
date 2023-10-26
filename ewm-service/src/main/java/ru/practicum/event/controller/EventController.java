package ru.practicum.event.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsClient;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;
    private final StatsClient client;

    @GetMapping
    List<EventShortDto> findEvents(@RequestParam(required = false) String text,
                                   @RequestParam(required = false) List<Long> categories,
                                   @RequestParam(defaultValue = "false") Boolean paid,
                                   @RequestParam(required = false) String rangeStart,
                                   @RequestParam(required = false) String rangeEnd,
                                   @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                   @RequestParam(required = false) String sort,
                                   @RequestParam(required = false, defaultValue = "0") int from,
                                   @RequestParam(required = false, defaultValue = "10") int size,
                                   HttpServletRequest request) {
        addEndpointHit(request);
        return eventService.findAllEvents(text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable Long id, HttpServletRequest request) {
        addEndpointHit(request);
        return eventService.getById(id);
    }

    private void addEndpointHit(HttpServletRequest request) {
        client.addEndpointHit(new EndpointHitDto(
                "ewm-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now())
        );
    }
}
