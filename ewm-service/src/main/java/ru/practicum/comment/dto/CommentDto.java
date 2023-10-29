package ru.practicum.comment.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.comment.model.CommentStatus;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CommentDto {
    String text;
    EventShortDto event;
    UserShortDto author;
    LocalDateTime created;
    Boolean isEdited;
    CommentStatus status;
}
