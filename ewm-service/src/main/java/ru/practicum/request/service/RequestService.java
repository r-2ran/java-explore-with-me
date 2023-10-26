package ru.practicum.request.service;

import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto addRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getAllByUserId(Long userId);

    List<ParticipationRequestDto> getAllByUserAndEvent(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long requestId, Long userId);

    EventRequestStatusUpdateResult updateRequest(EventRequestStatusUpdateRequest request,
                                                 Long eventId, Long userId);
}
