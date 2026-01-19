package com.julia.taskmanagementapp.service.comment;

import com.julia.taskmanagementapp.dto.comment.CommentDto;
import com.julia.taskmanagementapp.dto.comment.CreateCommentRequestDto;
import com.julia.taskmanagementapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    CommentDto create(CreateCommentRequestDto requestDto, User user);

    Page<CommentDto> getCommentsByTaskId(Long taskId, Long userId, Pageable pageable);
}
