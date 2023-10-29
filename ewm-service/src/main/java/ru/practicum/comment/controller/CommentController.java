package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdatedComment;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("events/{eventId}/comments")
public class CommentController {
    private final CommentService commentService;
    private static final String USER_ID = "User-Id";
    private static final String ID = "/{commentId}";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@Valid @RequestBody NewCommentDto commentDto,
                                 @RequestHeader(name = USER_ID) Long userId,
                                 @PathVariable(name = "eventId") Long eventId) {
        return commentService.addComment(commentDto, userId, eventId);
    }

    @GetMapping
    CommentDto getCommentByEventAndAuthor(@PathVariable(name = "eventId") Long eventId,
                                          @RequestHeader(name = USER_ID) Long userId) {
        return commentService.getCommentByEventAndAuthor(eventId, userId);
    }

    @GetMapping("/event")
    List<CommentDto> getAllByEvent(@PathVariable Long eventId) {
        return commentService.getAllByEvent(eventId);
    }

    @GetMapping("/user")
    List<CommentDto> getAllByAuthorId(@RequestHeader(name = USER_ID) Long userId) {
        return commentService.getAllByAuthorId(userId);
    }

    @PatchMapping(ID)
    CommentDto updateComment(@PathVariable(name = "commentId") Long commentId,
                             @Valid @RequestBody UpdatedComment comment) {
        return commentService.updateComment(commentId, comment);
    }

    @DeleteMapping(ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCommentById(@PathVariable(name = "commentId") Long commentId) {
        commentService.deleteCommentById(commentId);
    }
}
