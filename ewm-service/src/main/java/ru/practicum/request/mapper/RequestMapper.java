package ru.practicum.request.mapper;

import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;

import java.util.ArrayList;
import java.util.List;

public class RequestMapper {

    public static ParticipationRequestDto toDto(Request request) {
        return new ParticipationRequestDto(
                request.getCreated(),
                request.getEvent().getId(),
                request.getId(),
                request.getRequester().getId(),
                request.getStatus()
        );
    }

    public static List<ParticipationRequestDto> toDtoList(List<Request> requests) {
        List<ParticipationRequestDto> res = new ArrayList<>();
        for (Request request : requests) {
            res.add(toDto(request));
        }
        return res;
    }
}
