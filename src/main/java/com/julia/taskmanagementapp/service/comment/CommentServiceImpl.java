package com.julia.taskmanagementapp.service.comment;

import com.julia.taskmanagementapp.dto.comment.CommentDto;
import com.julia.taskmanagementapp.dto.comment.CreateCommentRequestDto;
import com.julia.taskmanagementapp.exception.EntityNotFoundException;
import com.julia.taskmanagementapp.mapper.CommentMapper;
import com.julia.taskmanagementapp.model.Comment;
import com.julia.taskmanagementapp.repository.CommentRepository;
import com.julia.taskmanagementapp.repository.TaskRepository;
import com.julia.taskmanagementapp.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto create(CreateCommentRequestDto requestDto, Long userId) {
        taskExists(requestDto.taskId());
        Comment comment = commentMapper.toModel(requestDto);
        comment.setUserId(userId);
        comment.setTimestamp(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    @Override
    public Page<CommentDto> getCommentsByTaskId(Pageable pageable, Long taskId) {
        taskExists(taskId);
        return commentRepository.findCommentsByTaskId(pageable, taskId)
                .map(commentMapper::toDto);
    }

    private void taskExists(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new EntityNotFoundException("There is no task by id: " + taskId);
        }
    }
}
