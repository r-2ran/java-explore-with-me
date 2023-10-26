package ru.practicum.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.AccessDeniedException;
import ru.practicum.exception.AlreadyExistException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.state.RequestStatus;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        Request request = new Request();
        request.setRequester(checkUser(userId));
        request.setEvent(checkEvent(eventId));
        request.setCreated(LocalDateTime.now());
        request.setStatus(RequestStatus.PENDING);
        requestRepository.save(request);
        return toDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getAllByUserId(Long userId) {
        return toDtoList(requestRepository.findAllByRequesterId(userId));
    }

    @Override
    public List<ParticipationRequestDto> getAllByUserAndEvent(Long userId, Long eventId) {
        return toDtoList(requestRepository.findAllByEventIdAndRequesterId(eventId, userId));
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

    @Override
    public EventRequestStatusUpdateResult updateRequest(EventRequestStatusUpdateRequest request,
                                                        Long eventId, Long userId) {
        checkUser(userId);
        Event event = checkEvent(eventId);
        if (!Objects.equals(userId, event.getInitiator().getId())) {
            throw new AccessDeniedException(String.format("user id %d don't have access to event %d",
                    userId, eventId));
        }

        List<Request> res = new ArrayList<>(requestRepository
                .findAllById(request.getRequestIds()));

        res.forEach(req -> {
            if (req.getStatus() != RequestStatus.PENDING) {
                throw new AlreadyExistException("," +
                        " ее статус " + req.getStatus());
            }
        });
        Integer confirmed = requestRepository
                .findAllByIdAndStatusEquals(eventId, RequestStatus.CONFIRMED.name()).size();
        if (Objects.equals(confirmed, event.getParticipantLimit())) {
            throw new AccessDeniedException("participation limit reached");
        }
        if (Objects.equals(request.getStatus(), RequestStatus.REJECTED.name())) {
            res.forEach(req -> {
                req.setStatus(RequestStatus.REJECTED);
                requestRepository.save(req);
            });
            return toResult(new ArrayList<>(), res);
        }
        return checkParticipation(res, event, confirmed);
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("no such user id %d", userId)));
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("no such event id %d", eventId)));
    }

    private EventRequestStatusUpdateResult checkParticipation(List<Request> requests, Event event,
                                                              Integer inputRequests) {
        Request request;
        List<Request> all = requestRepository.findAllByEventId(event.getId());
        List<Request> confirmed = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();

        for (int i = 0; i < requests.size(); i++) {
            if (inputRequests <= event.getParticipantLimit()) {
                request = requests.get(0);
                all.remove(request);
                request.setStatus(RequestStatus.CONFIRMED);
                Request resultReq = requestRepository.save(request);
                confirmed.add(resultReq);
                inputRequests++;
            } else {
                all.forEach(req -> {
                    req.setStatus(RequestStatus.REJECTED);
                    Request resultReq = requestRepository.save(req);
                    rejected.add(resultReq);
                });
                break;
            }
        }
        return toResult(confirmed, rejected);
    }
}
