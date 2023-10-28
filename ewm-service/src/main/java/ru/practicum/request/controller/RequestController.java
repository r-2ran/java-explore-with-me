package ru.practicum.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
@Validated
public class RequestController {
    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getAll(@PathVariable(name = "userId") Long userId) {
        return requestService.getAllByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable(name = "userId") Long userId,
                                              @RequestParam(value = "eventId", required = false) Long eventId) {
        return requestService.addRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable(name = "userId") Long userId,
                                                 @PathVariable(name = "requestId") Long requestId) {
        return requestService.cancelRequest(requestId, userId);
    }
}
