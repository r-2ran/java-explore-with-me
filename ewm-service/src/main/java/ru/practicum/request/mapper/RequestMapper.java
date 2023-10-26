package ru.practicum.request.mapper;

import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RequestMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ParticipationRequestDto toDto(Request request) {
        return new ParticipationRequestDto(
                request.getCreated().format(FORMATTER),
                request.getEvent().getId(),
                request.getId(),
                request.getRequester().getId(),
                request.getStatus().name()
        );
    }

    public static List<ParticipationRequestDto> toDtoList(List<Request> requests) {
        List<ParticipationRequestDto> res = new ArrayList<>();
        for (Request request : requests) {
            res.add(toDto(request));
        }
        return res;
    }

    public static EventRequestStatusUpdateResult toResult(List<Request> confirmed,
                                                          List<Request> rejected) {
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        confirmed.forEach(request ->
                confirmedRequests.add(RequestMapper.toDto(request)));
        rejected.forEach(request ->
                rejectedRequests.add(RequestMapper.toDto(request)));
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }
}
