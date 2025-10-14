package com.julia.taskmanagementapp.service.comment;

import com.julia.taskmanagementapp.dto.comment.CommentDto;
import com.julia.taskmanagementapp.dto.comment.CreateCommentRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    CommentDto create(CreateCommentRequestDto requestDto, Long userId);

    Page<CommentDto> getCommentsByTaskId(Pageable pageable, Long taskId);
}
