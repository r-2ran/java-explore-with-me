package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;
import ru.practicum.location.model.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {
    @Length(min = 20, max = 2000)
    @NotBlank
    String annotation;
    @Positive
    Long category;
    @Length(min = 20, max = 7000)
    @NotBlank
    String description;
    @NotBlank
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    String eventDate;
    @NotNull
    Location location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    @NotBlank
    @Length(min = 3, max = 120)
    String title;

}
