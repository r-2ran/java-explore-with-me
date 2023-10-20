package ru.practicum.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.service.EndpointHitService;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/hit")
public class EndpointHitController {
    private final EndpointHitService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@RequestBody EndpointHitDto hitDto) {
        service.addHit(hitDto);
    }
}
