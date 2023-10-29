package ru.practicum.comment.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.CommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin/comments/{commentId}")
public class AdminCommentController {
    private final CommentService commentService;

    @PatchMapping
    public CommentDto approveComment(@PathVariable Long commentId,
                                     @RequestParam(name = "approved") Boolean isApproved) {
        return commentService.approveCommentAdmin(commentId, isApproved);
    }
}
