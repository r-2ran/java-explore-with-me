package ru.practicum.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ViewStatsDto;
import ru.practicum.service.EndPointHitService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/stats")
public class ViewStatsController {
    private final EndPointHitService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatsDto> getStats(@RequestParam String start,
                                       @RequestParam String end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(defaultValue = "false") boolean unique) {
        return service.getStats(uris, unique, start, end);
    }
}
