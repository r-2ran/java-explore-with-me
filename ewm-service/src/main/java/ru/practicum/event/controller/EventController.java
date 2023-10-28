package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;
import ru.practicum.state.SortState;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class EventController {
    private final EventService eventService;
    private final String datePattern = "yyyy-MM-dd HH:mm:ss";

    @GetMapping
    public List<EventShortDto> findEvents(
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "categories", required = false) List<Long> categories,
            @RequestParam(value = "paid", required = false) Boolean paid,
            @RequestParam(value = "rangeStart", required = false)
            @DateTimeFormat(pattern = datePattern) LocalDateTime rangeStart,
            @RequestParam(value = "rangeEnd", required = false)
            @DateTimeFormat(pattern = datePattern) LocalDateTime rangeEnd,
            @RequestParam(value = "onlyAvailable", defaultValue = "false", required = false) boolean onlyAvailable,
            @RequestParam(value = "sort", required = false) SortState sort,
            @RequestParam(value = "from", defaultValue = "0", required = false) int from,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            HttpServletRequest request) {
        return eventService.getEventsPublic(text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable Long id, HttpServletRequest request) {
        return eventService.getEventByIdPublic(id, request);
    }
}
