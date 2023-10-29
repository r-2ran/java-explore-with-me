package ru.practicum.comment.mapper;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.UpdatedComment;
import ru.practicum.comment.model.Comment;

import static ru.practicum.event.mapper.EventMapper.*;
import static ru.practicum.user.mapper.UserMapper.toShortUserDto;

public class CommentMapper {
    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .text(comment.getText())
                .isEdited(comment.getIsEdited())
                .event(toFullDto(comment.getEvent()))
                .author(toShortUserDto(comment.getAuthor()))
                .created(comment.getCreated())
                .build();
    }

    public static Comment to(UpdatedComment commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .build();
    }
}
