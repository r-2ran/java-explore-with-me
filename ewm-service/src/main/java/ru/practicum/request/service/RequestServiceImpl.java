package ru.practicum.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.AccessDeniedException;
import ru.practicum.exception.ConflictRequestParamException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.state.RequestStatus;
import ru.practicum.state.State;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static ru.practicum.request.mapper.RequestMapper.*;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        Event event = checkEvent(eventId);
        User user = checkUser(userId);
        if (Objects.equals(userId, event.getInitiator().getId())) {
            throw new ConflictRequestParamException("master cannot be initiator");
        }
        if (!requestRepository.findAllByEventIdAndRequesterId(eventId, userId).isEmpty()) {
            throw new ConflictRequestParamException(String.format("user %d already in event", userId));
        }
        if (event.getState() != State.PUBLISHED) {
            throw new AccessDeniedException("event is not PUBLISHED");
        }
        if (event.getParticipantLimit() <= requestRepository.countByEventIdAndStatus(
                eventId, RequestStatus.CONFIRMED) && event.getParticipantLimit() != 0) {
            throw new AccessDeniedException(String.format("event %d reached participant limit", eventId));
        }
        Request request = Request.builder()
                .event(event)
                .requester(user)
                .created(LocalDateTime.now())
                .build();
        if (event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }
        if (!event.getRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        return toDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getAllByUserId(Long userId) {
        checkUser(userId);
        return toDtoList(requestRepository.findAllByRequesterId(userId));
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long requestId, Long userId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("request id = %d not exist", requestId)));

        if (!Objects.equals(request.getRequester().getId(), userId)) {
            throw new AccessDeniedException(String.format("user id = %d not have access", userId));
        }

        request.setStatus(RequestStatus.CANCELED);
        return toDto(requestRepository.save(request));
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("no such user id %d", userId)));
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("no such event id %d", eventId)));
    }
}
