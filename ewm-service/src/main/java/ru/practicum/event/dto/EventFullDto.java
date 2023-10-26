package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.location.model.Location;
import ru.practicum.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
    @NotBlank
    String annotation;
    @NotNull
    CategoryDto category;
    Long confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    String createdOn;
    String description;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    String eventDate;
    Long id;
    @NotNull
    UserShortDto initiator;
    @NotNull
    Location location;
    @NotNull
    Boolean paid;
    Integer participantLimit;
    String publishedOn;
    Boolean requestModeration;
    String state;
    @NotNull
    String title;
    Long views;

    public EventFullDto(String annotation, CategoryDto category, Long confirmedRequests, String createdOn,
                        String description, String eventDate, Long id, UserShortDto initiator,
                        Location location, Boolean paid, Integer participantLimit, String publishedOn,
                        Boolean requestModeration, String state, String title) {
        this.annotation = annotation;
        this.category = category;
        this.confirmedRequests = confirmedRequests;
        this.createdOn = createdOn;
        this.description = description;
        this.eventDate = eventDate;
        this.id = id;
        this.initiator = initiator;
        this.location = location;
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.publishedOn = publishedOn;
        this.requestModeration = requestModeration;
        this.state = state;
        this.title = title;
    }
}
