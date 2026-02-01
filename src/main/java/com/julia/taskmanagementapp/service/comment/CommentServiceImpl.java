package com.julia.taskmanagementapp.service.comment;

import com.julia.taskmanagementapp.dto.comment.CommentDto;
import com.julia.taskmanagementapp.dto.comment.CreateCommentRequestDto;
import com.julia.taskmanagementapp.event.comment.factory.CommentEventFactory;
import com.julia.taskmanagementapp.event.comment.factory.CommentEventType;
import com.julia.taskmanagementapp.exception.EntityNotFoundException;
import com.julia.taskmanagementapp.mapper.CommentMapper;
import com.julia.taskmanagementapp.model.Comment;
import com.julia.taskmanagementapp.model.Project;
import com.julia.taskmanagementapp.model.Task;
import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.repository.CommentRepository;
import com.julia.taskmanagementapp.repository.TaskRepository;
import com.julia.taskmanagementapp.repository.UserRepository;
import com.julia.taskmanagementapp.service.project.ProjectPermissionService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final ProjectPermissionService projectPermissionService;
    private final ApplicationEventPublisher publisher;
    private final CommentEventFactory commentEventFactory;

    @Transactional
    @Override
    public CommentDto create(CreateCommentRequestDto requestDto, User user) {
        Task task = getTaskById(requestDto.taskId());
        Project project = projectPermissionService.getProjectByIdIfCreatorOrCollaborator(
                task.getProjectId(), user.getId()
        );

        Comment comment = commentMapper.toModel(requestDto);
        comment.setUserId(user.getId());
        comment.setTimestamp(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);

        notifyUsers(
                CommentEventType.COMMENT_CREATED,
                project,
                task,
                savedComment,
                user
        );

        return commentMapper.toDto(savedComment);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CommentDto> getCommentsByTaskId(Long taskId, Long userId, Pageable pageable) {
        Task task = getTaskById(taskId);
        projectPermissionService.checkProjectIfCreatorOrCollaborator(
                task.getProjectId(), userId
        );
        return commentRepository.findCommentsByTaskId(pageable, taskId)
                .map(commentMapper::toDto);
    }

    @Transactional(readOnly = true)
    private Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(
                () -> new EntityNotFoundException(
                        "There is no task by id: " + taskId
                )
        );
    }

    private void notifyUsers(
            CommentEventType type,
            Project project,
            Task task,
            Comment comment,
            User commentCreator
    ) {
        User assignee = getUser(task.getAssigneeId());

        publisher.publishEvent(
                    commentEventFactory.create(
                            type,
                            project,
                            task,
                            comment,
                            commentCreator,
                            assignee
                    )
        );
    }

    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        "There is no user by id: " + id
                )
        );
    }
}
