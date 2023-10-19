package ru.practicum.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.service.EndPointHitService;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/hit")
public class EndPointHitController {
    private final EndPointHitService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@RequestBody EndpointHitDto hitDto) {
        service.addHit(hitDto);
    }
}
