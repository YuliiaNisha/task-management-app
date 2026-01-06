package com.julia.taskmanagementapp.service.task;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.julia.taskmanagementapp.dto.task.CreateTaskRequestDto;
import com.julia.taskmanagementapp.dto.task.TaskDto;
import com.julia.taskmanagementapp.dto.task.TaskSearchParameters;
import com.julia.taskmanagementapp.dto.task.TaskSearchParametersWithAssigneeId;
import com.julia.taskmanagementapp.dto.task.UpdateTaskRequestDto;
import com.julia.taskmanagementapp.event.task.LabelAssignedToTaskEvent;
import com.julia.taskmanagementapp.event.task.LabelRemovedFromTaskEvent;
import com.julia.taskmanagementapp.event.task.TaskCreatedEvent;
import com.julia.taskmanagementapp.event.task.TaskDeletedEvent;
import com.julia.taskmanagementapp.event.task.TaskUpdatedEvent;
import com.julia.taskmanagementapp.event.task.factory.TaskEventFactory;
import com.julia.taskmanagementapp.event.task.factory.TaskEventType;
import com.julia.taskmanagementapp.exception.EntityAlreadyExistsException;
import com.julia.taskmanagementapp.exception.EntityNotFoundException;
import com.julia.taskmanagementapp.mapper.TaskMapper;
import com.julia.taskmanagementapp.model.Label;
import com.julia.taskmanagementapp.model.Project;
import com.julia.taskmanagementapp.model.Task;
import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.repository.LabelRepository;
import com.julia.taskmanagementapp.repository.SpecificationBuilder;
import com.julia.taskmanagementapp.repository.TaskRepository;
import com.julia.taskmanagementapp.service.label.LabelPermissionService;
import com.julia.taskmanagementapp.service.project.ProjectPermissionService;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {
    private static final Long PROJECT_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final Long ASSIGNEE_ID = 1L;
    private static final Long TASK_ID = 1L;
    private static final Long LABEL_ID = 1L;
    @InjectMocks
    private TaskServiceImpl taskService;
    @Mock
    private TaskMapper taskMapper;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private LabelRepository labelRepository;
    @Mock
    private ProjectPermissionService projectPermissionService;
    @Mock
    private LabelPermissionService labelPermissionService;
    @Mock
    private ApplicationEventPublisher publisher;
    @Mock
    private TaskEventFactory taskEventFactory;
    @Mock
    private SpecificationBuilder<Task, TaskSearchParametersWithAssigneeId> specificationBuilder;
    private Label label;
    private Label labelSecond;
    private TaskDto taskDto;
    private CreateTaskRequestDto createTaskRequestDto;
    private UpdateTaskRequestDto updateTaskRequestDto;
    private Task task;
    private Task savedTask;
    private User assignee;
    private Project project;
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
        taskDto = new TaskDto(
                TASK_ID, "task",
                "description", Task.Priority.LOW.name(),
                Task.Status.NOT_STARTED.name(), LocalDate.now(),
                PROJECT_ID, ASSIGNEE_ID,
                Set.of(LABEL_ID));

        createTaskRequestDto = new CreateTaskRequestDto(
                "Test task", "description", "HIGH",
                LocalDate.of(2025, 12, 31),
                1L, 1L, Set.of(1L)
        );

        updateTaskRequestDto = new UpdateTaskRequestDto(
                "Test task updated",
                null, null, null, null,
                null, null, null
        );

        label = new Label();
        label.setId(1L);

        labelSecond = new Label();
        labelSecond.setId(2L);

        Set<Label> labels = new HashSet<>();
        labels.add(label);

        task = new Task();
        task.setAssigneeId(1L);
        task.setId(1L);
        task.setName("task");
        task.setDueDate(LocalDate.now());
        task.setProjectId(1L);
        task.setLabels(labels);

        savedTask = new Task();
        savedTask.setAssigneeId(1L);
        savedTask.setId(1L);
        savedTask.setName("task");
        savedTask.setDueDate(LocalDate.now());
        savedTask.setProjectId(1L);

        assignee = new User();
        assignee.setEmail("email");
        assignee.setFirstName("First");
        assignee.setId(10L);

        project = new Project();
        project.setName("project");
    }

    @Test
    void create_validRequest_returnTaskDto() {
        TaskCreatedEvent taskCreatedEvent = new TaskCreatedEvent(
                assignee.getEmail(),
                assignee.getFirstName(),
                task.getId().toString(),
                task.getName(),
                project.getName(),
                task.getDueDate().toString()
        );

        when(projectPermissionService.getProjectByIdIfCreator(
                createTaskRequestDto.projectId(), userId
        )).thenReturn(project);
        doNothing().when(projectPermissionService)
                .checkProjectIfCollaborator(PROJECT_ID, USER_ID);
        doNothing().when(labelPermissionService)
                .checkLabelsIfCreator(Set.of(1L), userId);
        when(taskMapper.toModel(createTaskRequestDto)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(savedTask);
        when(taskEventFactory.create(
                TaskEventType.TASK_CREATED, project, savedTask
        )).thenReturn(taskCreatedEvent);
        when(taskMapper.toDto(savedTask)).thenReturn(taskDto);

        TaskDto actual = taskService.create(createTaskRequestDto, userId);

        assertEquals(taskDto, actual);
        verify(publisher).publishEvent(taskCreatedEvent);
    }

    @Test
    void getTasksForProject_validRequest_returnsPageTaskDto() {
        Long projectId = 1L;
        Page<Task> tasks = new PageImpl<>(List.of(task));
        Pageable pageable = PageRequest.of(0, 10);

        doNothing().when(projectPermissionService).checkProjectIfCreatorOrCollaborator(
                projectId, userId
        );
        when(taskRepository.findByProjectId(
                projectId, pageable
        ))
                .thenReturn(tasks);
        when(taskMapper.toDto(task)).thenReturn(taskDto);

        Page<TaskDto> actual = taskService.getTasksForProject(
                projectId, userId, pageable
        );
        Page<TaskDto> taskDtos = new PageImpl<>(List.of(taskDto));
        assertEquals(taskDtos, actual);
    }

    @Test
    void getTaskById_validId_returnTaskDto() {
        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));
        doNothing().when(projectPermissionService)
                .checkProjectIfCreatorOrCollaborator(task.getProjectId(), userId);
        when(taskMapper.toDto(task)).thenReturn(taskDto);

        TaskDto actual = taskService.getTaskById(1L, userId);

        assertEquals(taskDto, actual);
    }

    @Test
    void getTaskById_invalidId_throwException() {
        when(taskRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> taskService.getTaskById(1L, USER_ID));
    }

    @Test
    void update_validRequest_returnTaskDto() {
        TaskUpdatedEvent taskUpdatedEvent = new TaskUpdatedEvent(
                assignee.getEmail(),
                assignee.getFirstName(),
                savedTask.getId().toString(),
                savedTask.getName(),
                project.getName(),
                savedTask.getDueDate().toString()
        );

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));
        when(projectPermissionService.getProjectByIdIfCreator(
                task.getProjectId(), userId))
                .thenReturn(project);
        doNothing().when(taskMapper).updateTask(task, updateTaskRequestDto);
        when(taskRepository.save(task)).thenReturn(savedTask);
        when(taskEventFactory.create(
                TaskEventType.TASK_UPDATED, project, savedTask
        )).thenReturn(taskUpdatedEvent);
        when(taskMapper.toDto(savedTask)).thenReturn(taskDto);

        TaskDto actual = taskService.update(1L, updateTaskRequestDto, userId);

        assertEquals(taskDto, actual);
        verify(publisher).publishEvent(taskUpdatedEvent);
    }

    @Test
    void delete_validId_void() {
        TaskDeletedEvent taskDeletedEvent = new TaskDeletedEvent(
                assignee.getEmail(),
                assignee.getFirstName(),
                savedTask.getName(),
                project.getName()
        );
        when(taskRepository.findById(task.getId()))
                .thenReturn(Optional.of(task));
        when(projectPermissionService.getProjectByIdIfCreator(
                task.getProjectId(), 1L))
                .thenReturn(project);
        doNothing().when(taskRepository).delete(task);
        when(taskEventFactory.create(
                TaskEventType.TASK_DELETED, project, task
        )).thenReturn(taskDeletedEvent);

        assertDoesNotThrow(() -> taskService.delete(
                task.getId(), 1L
        ));
        verify(publisher).publishEvent(taskDeletedEvent);
    }

    @Test
    void assignLabelToTask_validId_returnsTaskDto() {
        LabelAssignedToTaskEvent labelAssignedToTaskEvent = new LabelAssignedToTaskEvent(
                assignee.getEmail(),
                assignee.getFirstName(),
                task.getId().toString(),
                savedTask.getName(),
                project.getName(),
                task.getDueDate().toString()
        );
        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));
        when(projectPermissionService.getProjectByIdIfCreator(
                task.getProjectId(), 1L)).thenReturn(project);
        when(labelPermissionService.findLabelIfCreator(
                2L, 1L)).thenReturn(labelSecond);
        when(taskRepository.save(task)).thenReturn(savedTask);
        when(taskEventFactory.create(
                TaskEventType.LABEL_ASSIGNED_TO_TASK, project, savedTask
        )).thenReturn(labelAssignedToTaskEvent);
        when(taskMapper.toDto(savedTask)).thenReturn(taskDto);

        TaskDto actual = taskService.assignLabelToTask(1L, 2L, 1L);

        assertEquals(taskDto, actual);

        verify(publisher).publishEvent(labelAssignedToTaskEvent);
    }

    @Test
    void assignLabelToTask_labelExists_throwsException() {
        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));
        when(projectPermissionService.getProjectByIdIfCreator(
                task.getProjectId(), 1L)).thenReturn(project);
        when(labelPermissionService.findLabelIfCreator(
                1L, 1L)).thenReturn(label);

        assertThrows(EntityAlreadyExistsException.class,
                () -> taskService.assignLabelToTask(1L, 1L, 1L));
    }

    @Test
    void removeLabelFromTask_validId_returnsTaskDto() {
        LabelRemovedFromTaskEvent labelRemovedFromTaskEvent = new LabelRemovedFromTaskEvent(
                assignee.getEmail(),
                assignee.getFirstName(),
                task.getId().toString(),
                task.getName(),
                project.getName(),
                task.getDueDate().toString()
        );
        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(task));
        when(projectPermissionService.getProjectByIdIfCreator(
                task.getProjectId(), 1L)).thenReturn(project);
        when(labelRepository.findByIdAndCreatorId(
                1L, 1L
        )).thenReturn(Optional.of(label));
        when(taskRepository.save(task)).thenReturn(savedTask);
        when(taskEventFactory.create(
                TaskEventType.LABEL_REMOVED_FROM_TASK, project, savedTask
        )).thenReturn(labelRemovedFromTaskEvent);
        when(taskMapper.toDto(savedTask)).thenReturn(taskDto);

        TaskDto actual = taskService.removeLabelFromTask(1L, 1L, 1L);

        assertEquals(taskDto, actual);
        verify(publisher).publishEvent(labelRemovedFromTaskEvent);
    }

    @Test
    void search_validRequest_returnPageTaskDto() {
        TaskSearchParameters taskSearchParameters = new TaskSearchParameters(
                "name", null, null, null, null
        );
        TaskSearchParametersWithAssigneeId searchParamsWithAssigneeId =
                new TaskSearchParametersWithAssigneeId();
        searchParamsWithAssigneeId.setName("name");
        searchParamsWithAssigneeId.setAssigneeId(1L);
        Pageable pageable = PageRequest.of(0, 10);

        Page<Task> page = new PageImpl<>(List.of(task));

        when(taskMapper.toParamsWithAssignee(taskSearchParameters))
                .thenReturn(searchParamsWithAssigneeId);
        when(specificationBuilder.build(searchParamsWithAssigneeId))
                .thenReturn(Specification.unrestricted());
        when(taskRepository.findAll(
                Specification.unrestricted(),
                pageable
        )).thenReturn(page);
        when(taskMapper.toDto(task)).thenReturn(taskDto);

        Page<TaskDto> actual = taskService.search(
                taskSearchParameters, pageable, 1L
        );

        assertEquals(1, actual.getTotalElements());
        assertEquals(taskDto, actual.getContent().get(0));
    }
}
