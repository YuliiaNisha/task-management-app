package com.julia.taskmanagementapp.service.comment;

import com.julia.taskmanagementapp.dto.comment.CommentDto;
import com.julia.taskmanagementapp.dto.comment.CreateCommentRequestDto;
import com.julia.taskmanagementapp.exception.EntityNotFoundException;
import com.julia.taskmanagementapp.mapper.CommentMapper;
import com.julia.taskmanagementapp.model.Comment;
import com.julia.taskmanagementapp.model.Task;
import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.repository.CommentRepository;
import com.julia.taskmanagementapp.repository.TaskRepository;
import com.julia.taskmanagementapp.repository.UserRepository;
import com.julia.taskmanagementapp.service.project.ProjectPermissionService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final CommentMapper commentMapper;
    private final ProjectPermissionService projectPermissionService;

    @Override
    public CommentDto create(CreateCommentRequestDto requestDto, Long userId) {
        Task task = getTaskById(requestDto.taskId());
        projectPermissionService.checkProjectIfCreatorOrCollaborator(
                task.getProjectId(), userId);
        Comment comment = commentMapper.toModel(requestDto);
        comment.setUserId(userId);
        comment.setTimestamp(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    @Override
    public Page<CommentDto> getCommentsByTaskId(Long taskId, User user, Pageable pageable) {
        Task task = getTaskById(taskId);
        projectPermissionService.checkProjectIfCreatorOrCollaborator(
                task.getProjectId(), user.getId()
        );
        return commentRepository.findCommentsByTaskId(pageable, taskId)
                .map(commentMapper::toDto);
    }

    private Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(
                () -> new EntityNotFoundException(
                        "There is no task by id: " + taskId
                )
        );
    }
}
