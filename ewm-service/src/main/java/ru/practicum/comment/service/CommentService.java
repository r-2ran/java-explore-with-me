package ru.practicum.comment.service;

import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdatedComment;

import java.util.List;

@Service
public interface CommentService {
    CommentDto addComment(NewCommentDto commentDto, Long userId, Long eventId);

    CommentDto getCommentByEventAndAuthor(Long eventId, Long userId);

    List<CommentDto> getAllByEvent(Long eventID);

    List<CommentDto> getAllByAuthorId(Long userId);

    CommentDto updateComment(Long commentId, UpdatedComment commentDto);

    void deleteCommentById(Long commentId);

    CommentDto approveCommentAdmin(Long commentId, Boolean isApproved);
}
