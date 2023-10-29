package ru.practicum.event.mapper;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;

import java.time.format.DateTimeFormatter;
import java.util.*;

import static ru.practicum.category.mapper.CategoryMapper.toCategoryDto;
import static ru.practicum.user.mapper.UserMapper.toShortUserDto;
import static ru.practicum.location.mapper.LocationMapper.*;

public class EventMapper {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static EventShortDto toShortDto(Event event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests().size())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(toShortUserDto((event.getInitiator())))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static EventShortDto toFullDto(EventFullDto event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(event.getCategory())
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(event.getInitiator())
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static Set<EventShortDto> toShortEventDtoSet(List<Event> events) {
        Set<EventShortDto> result = new HashSet<>();
        for (Event event : events) {
            result.add(toShortDto(event));
        }
        return result;
    }

    public static List<EventShortDto> toShortEventDtoList(List<Event> events) {
        List<EventShortDto> result = new ArrayList<>();
        for (Event event : events) {
            result.add(toShortDto(event));
        }
        return result;
    }

    public static EventFullDto toFull(Event event) {
        EventFullDto res = EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(toCategoryDto(event.getCategory()))
                .createdOn(event.getCreated())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(toShortUserDto(event.getInitiator()))
                .location(toLocationDto((event.getLocation())))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
        if (!Objects.equals(event.getConfirmedRequests(), null)) {
            res.setConfirmedRequests(event.getConfirmedRequests().size());
        } else {
            res.setConfirmedRequests(0);
        }
        return res;
    }
}
