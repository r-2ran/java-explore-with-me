package ru.practicum.request.service;

import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto addRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getAllByUserId(Long userId);

    ParticipationRequestDto cancelRequest(Long requestId, Long userId);
}
