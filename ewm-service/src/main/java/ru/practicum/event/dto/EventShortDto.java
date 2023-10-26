package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {
    Long id;
    @NotBlank
    String annotation;
    @NotNull
    CategoryDto category;
    Long confirmedRequests;
    @NotBlank
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    String eventDate;
    @NotNull
    UserShortDto initiator;
    @NotNull
    Boolean paid;
    @NotBlank
    String title;
    Long views;

    public EventShortDto(Long id, String annotation, CategoryDto category, Long confirmedRequests,
                         String eventDate, UserShortDto initiator, Boolean paid, String title) {
        this.id = id;
        this.annotation = annotation;
        this.category = category;
        this.confirmedRequests = confirmedRequests;
        this.eventDate = eventDate;
        this.initiator = initiator;
        this.paid = paid;
        this.title = title;
    }
}
