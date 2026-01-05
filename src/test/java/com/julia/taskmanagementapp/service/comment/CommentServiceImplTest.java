package com.julia.taskmanagementapp.service.comment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.julia.taskmanagementapp.dto.comment.CommentDto;
import com.julia.taskmanagementapp.dto.comment.CreateCommentRequestDto;
import com.julia.taskmanagementapp.event.comment.CommentCreatedEvent;
import com.julia.taskmanagementapp.event.comment.factory.CommentEventFactory;
import com.julia.taskmanagementapp.event.comment.factory.CommentEventType;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private ProjectPermissionService projectPermissionService;
    @Mock
    private ApplicationEventPublisher publisher;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentEventFactory commentEventFactory;
    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void create_validRequest_returnsCommentDto() {
        Task task = new Task();
        task.setId(1L);
        task.setProjectId(1L);
        task.setAssigneeId(2L);
        task.setName("name");
        User user = new User();
        user.setId(1L);
        User assignee = new User();
        assignee.setEmail("assigneeEmail");
        User projectCreator = new User();
        projectCreator.setEmail("projectCreatorEmail");
        User commentCreator = new User();
        commentCreator.setEmail("commentCreatorEmail");
        commentCreator.setFirstName("First");
        commentCreator.setLastName("Last");
        Project project = new Project();
        project.setCreator(projectCreator);
        project.setName("name");

        Comment comment = new Comment();
        comment.setId(100L);
        Comment savedComment = new Comment();
        savedComment.setId(100L);
        savedComment.setText("text");
        CommentCreatedEvent commentCreatedEvent = new CommentCreatedEvent(
                Map.of("commentCreatorEmail", "First"),
                comment.getId().toString(),
                comment.getText(),
                project.getName(),
                task.getName(),
                "First Last"
        );

        CreateCommentRequestDto createCommentRequestDto =
                new CreateCommentRequestDto(1L, "text");

        when(taskRepository.findById(createCommentRequestDto.taskId()))
                .thenReturn(Optional.of(task));
        when(projectPermissionService.getProjectByIdIfCreatorOrCollaborator(
                task.getProjectId(), user.getId()))
                .thenReturn(project);
        when(commentMapper.toModel(createCommentRequestDto))
                .thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(savedComment);
        when(userRepository.findById(task.getAssigneeId()))
                .thenReturn(Optional.of(assignee));
        when(commentEventFactory.create(
                CommentEventType.COMMENT_CREATED,
                project,
                task,
                savedComment,
                user,
                assignee
        )).thenReturn(commentCreatedEvent);
        CommentDto commentDto = new CommentDto(
                1L, 1L, 1L,
                "text", LocalDateTime.now());
        when(commentMapper.toDto(savedComment)).thenReturn(commentDto);

        CommentDto actual = commentService.create(createCommentRequestDto, user);

        assertNotNull(actual);
        assertEquals(commentDto.id(), actual.id());

        verify(commentRepository).save(comment);
        verify(publisher).publishEvent(commentCreatedEvent);
    }

    @Test
    void getCommentsByTaskId_validId_returnsPageCommentDto() {

        Task task = new Task();
        task.setId(1L);
        task.setProjectId(1L);
        Comment comment = new Comment();
        Page<Comment> page = new PageImpl<>(List.of(comment));
        CommentDto commentDto = new CommentDto(
                1L, 1L, 1L,
                "comment", LocalDateTime.now()
        );

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));
        doNothing().when(projectPermissionService).checkProjectIfCreatorOrCollaborator(
                task.getProjectId(), 1L);
        when(commentRepository.findCommentsByTaskId(
                PageRequest.of(0, 10),
                1L))
                .thenReturn(page);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        Page<CommentDto> actual = commentService.getCommentsByTaskId(
                1L, 1L,
                PageRequest.of(0, 10));

        assertNotNull(actual);
        assertEquals(1, actual.getContent().size());
    }
}
