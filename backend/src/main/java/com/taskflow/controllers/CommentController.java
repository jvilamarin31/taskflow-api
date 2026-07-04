package com.taskflow.controllers;

import com.taskflow.dtos.requests.comments.CreateCommentRequest;
import com.taskflow.dtos.requests.comments.DeleteCommentRequest;
import com.taskflow.dtos.requests.comments.DetailCommentRequest;
import com.taskflow.dtos.requests.comments.ListCommentsRequest;
import com.taskflow.dtos.responses.comments.DetailCommentResponse;
import com.taskflow.models.UserModel;
import com.taskflow.services.ICommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/comments")
public class CommentController {

    private final ICommentService commentService;

    public CommentController(ICommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<Void> createComment(@AuthenticationPrincipal UserModel user, @RequestBody @Valid CreateCommentRequest request) {
        commentService.createComment(user.getId(), request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/detail")
    public ResponseEntity<DetailCommentResponse> getComment(@AuthenticationPrincipal UserModel user, @RequestBody @Valid DetailCommentRequest request) {
        return ResponseEntity.ok(commentService.getComment(user.getId(), request));
    }

    @GetMapping
    public ResponseEntity<List<DetailCommentResponse>> getComments(@AuthenticationPrincipal UserModel user, @RequestBody @Valid ListCommentsRequest request) {
        return ResponseEntity.ok(commentService.getComments(user.getId(), request));
    }

    @DeleteMapping
    public ResponseEntity<Void> DeleteComment(@AuthenticationPrincipal UserModel user, @RequestBody @Valid DeleteCommentRequest commentRequest) {
        commentService.DeleteComment(user.getId(), commentRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
