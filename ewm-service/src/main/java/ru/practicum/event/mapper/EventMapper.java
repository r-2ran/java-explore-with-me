package ru.practicum.event.mapper;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.location.model.Location;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ru.practicum.category.mapper.CategoryMapper.toCategoryDto;
import static ru.practicum.user.mapper.UserMapper.toShortUserDto;

public class EventMapper {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static EventShortDto toShortDto(Event event) {
        return new EventShortDto(
                event.getId(),
                event.getAnnotation(),
                toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getEventDate().format(FORMATTER),
                toShortUserDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle()
        );
    }

    public static Set<EventShortDto> toShortEventDtoSet(Set<Event> events) {
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

    public static Event to(NewEventDto eventDto) {
        return new Event(
                eventDto.getAnnotation(),
                eventDto.getDescription(),
                eventDto.getTitle(),
                LocalDateTime.parse(eventDto.getEventDate(), FORMATTER),
                eventDto.getLocation().getLat(),
                eventDto.getLocation().getLon(),
                eventDto.getPaid(),
                eventDto.getParticipantLimit(),
                eventDto.getRequestModeration()
        );
    }

    public static EventFullDto toFull(Event event) {
        return new EventFullDto(
                event.getAnnotation(),
                toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getPublishedOn().format(FORMATTER),
                event.getDescription(),
                event.getEventDate().format(FORMATTER),
                event.getId(),
                toShortUserDto(event.getInitiator()),
                new Location(event.getLat(), event.getLon()),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn().format(FORMATTER),
                event.getRequestModeration(),
                event.getState().name(),
                event.getTitle()
        );
    }

    public static List<EventFullDto> toFullEventDtoList(List<Event> events) {
        List<EventFullDto> result = new ArrayList<>();
        for (Event event : events) {
            result.add(toFull(event));
        }
        return result;
    }
}
