package com.julia.taskmanagementapp.controller;

import com.julia.taskmanagementapp.dto.comment.CommentDto;
import com.julia.taskmanagementapp.dto.comment.CreateCommentRequestDto;
import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.service.comment.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(
            @RequestBody @Valid CreateCommentRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        return commentService.create(requestDto, user.getId());
    }

    @GetMapping
    public Page<CommentDto> getCommentsByTaskId(
            Pageable pageable,
            @RequestParam Long taskId
    ) {
        return commentService.getCommentsByTaskId(pageable, taskId);
    }
}
