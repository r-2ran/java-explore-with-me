package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdatedComment;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentStatus;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.comment.mapper.CommentMapper.*;
import static ru.practicum.event.mapper.EventMapper.toFull;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public CommentDto addComment(NewCommentDto commentDto, Long userId, Long eventId) {
        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .author(checkUser(userId))
                .event(toFull(checkEvent(eventId)))
                .isEdited(false)
                .created(LocalDateTime.now())
                .status(CommentStatus.PENDING)
                .build();
        return toDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto getCommentByEventAndAuthor(Long eventId, Long userId) {
        checkEvent(eventId);
        checkUser(userId);
        return toDto(commentRepository.findByEventIdAndAuthorId(eventId, userId));
    }

    @Override
    public List<CommentDto> getAllByEvent(Long eventID) {
        checkEvent(eventID);
        return commentRepository.findAllByEventId(eventID).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getAllByAuthorId(Long userId) {
        checkUser(userId);
        return commentRepository.findAllByAuthorId(userId).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto updateComment(Long commentId, UpdatedComment commentDto) {
        Comment comment = checkComment(commentId);
        comment.setText(commentDto.getText());
        comment.setIsEdited(true);
        return toDto(commentRepository.save(comment));
    }

    @Override
    public void deleteCommentById(Long commentId) {
        checkComment(commentId);
        commentRepository.deleteById(commentId);
    }


    @Override
    public CommentDto approveCommentAdmin(Long commentId, Boolean isApproved) {
        Comment comment = checkComment(commentId);
        if (isApproved) {
            comment.setStatus(CommentStatus.APPROVED);
        } else {
            comment.setStatus(CommentStatus.CANCELED);
            commentRepository.deleteById(commentId);
            return new CommentDto();
        }
        return toDto(commentRepository.save(comment));
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("not found user id = %d", userId)));
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("not found event id = %d", eventId)));
    }

    private Comment checkComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("not found comment id = %d", commentId)));
    }
}
