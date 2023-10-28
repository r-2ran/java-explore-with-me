package ru.practicum.controller;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ViewStatsDto;
import ru.practicum.service.EndpointHitService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/stats")
public class ViewStatsController {
    private final EndpointHitService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatsDto> getStats(@RequestParam(required = false)
                                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                       @RequestParam(required = false)
                                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(defaultValue = "false") boolean unique) {
        return service.getStats(uris, unique, start, end);
    }
}
