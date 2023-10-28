package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.state.StateActionUser;

import javax.validation.constraints.Future;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventUserRequest {
    @Length(min = 20, max = 2000)
    String annotation;
    Long category;
    @Length(min = 20, max = 7000)
    String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Future
    LocalDateTime eventDate;
    LocationDto location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    StateActionUser stateAction;
    @Length(min = 3, max = 120)
    String title;
}
