package com.julia.taskmanagementapp.service.task;

import com.julia.taskmanagementapp.dto.task.CreateTaskRequestDto;
import com.julia.taskmanagementapp.dto.task.TaskDto;
import com.julia.taskmanagementapp.dto.task.TaskSearchParameters;
import com.julia.taskmanagementapp.dto.task.TaskSearchParametersWithAssigneeId;
import com.julia.taskmanagementapp.dto.task.UpdateTaskRequestDto;
import com.julia.taskmanagementapp.event.task.factory.TaskEventFactory;
import com.julia.taskmanagementapp.event.task.factory.TaskEventType;
import com.julia.taskmanagementapp.exception.EntityAlreadyExistsException;
import com.julia.taskmanagementapp.exception.EntityNotFoundException;
import com.julia.taskmanagementapp.exception.ForbiddenAccessException;
import com.julia.taskmanagementapp.mapper.TaskMapper;
import com.julia.taskmanagementapp.model.Label;
import com.julia.taskmanagementapp.model.Project;
import com.julia.taskmanagementapp.model.Task;
import com.julia.taskmanagementapp.repository.LabelRepository;
import com.julia.taskmanagementapp.repository.SpecificationBuilder;
import com.julia.taskmanagementapp.repository.TaskRepository;
import com.julia.taskmanagementapp.service.label.LabelPermissionService;
import com.julia.taskmanagementapp.service.project.ProjectPermissionService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskMapper taskMapper;
    private final TaskRepository taskRepository;
    private final LabelRepository labelRepository;
    private final ProjectPermissionService projectPermissionService;
    private final LabelPermissionService labelPermissionService;
    private final ApplicationEventPublisher publisher;
    private final TaskEventFactory taskEventFactory;
    private final SpecificationBuilder<
            Task, TaskSearchParametersWithAssigneeId
            > specificationBuilder;

    @Transactional
    @Override
    public TaskDto create(CreateTaskRequestDto requestDto, Long userId) {
        final Project project = projectPermissionService.getProjectByIdIfCreator(
                requestDto.projectId(), userId
        );
        projectPermissionService.checkProjectIfCollaborator(
                requestDto.projectId(), requestDto.assigneeId()
        );

        Set<Long> labelIds = requestDto.labelIds();
        if (labelIds != null && !labelIds.isEmpty()) {
            labelPermissionService.checkLabelsIfCreator(labelIds, userId);
        }

        Task task = taskMapper.toModel(requestDto);
        task.setStatus(Task.Status.NOT_STARTED);
        Task savedTask = taskRepository.save(task);

        notifyUser(TaskEventType.TASK_CREATED, project, savedTask);

        return taskMapper.toDto(savedTask);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<TaskDto> getTasksForProject(Long projectId, Long userId, Pageable pageable) {
        projectPermissionService.checkProjectIfCreatorOrCollaborator(
                projectId, userId
        );
        return taskRepository.findByProjectId(projectId, pageable)
                .map(taskMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public TaskDto getTaskById(Long id, Long userId) {
        Task task = findTaskById(id);
        projectPermissionService.checkProjectIfCreatorOrCollaborator(
                task.getProjectId(), userId
        );
        return taskMapper.toDto(task);
    }

    @Transactional
    @Override
    public TaskDto update(Long id, UpdateTaskRequestDto requestDto, Long userId) {
        Task task = findTaskById(id);
        Project project = projectPermissionService.getProjectByIdIfCreator(
                task.getProjectId(), userId
        );

        Set<Long> labelIds = requestDto.labelIds();
        if (labelIds != null && !labelIds.isEmpty()) {
            labelPermissionService.checkLabelsIfCreator(labelIds, userId);
        }

        taskMapper.updateTask(task, requestDto);
        Task savedTask = taskRepository.save(task);

        notifyUser(TaskEventType.TASK_UPDATED, project, savedTask);

        return taskMapper.toDto(savedTask);
    }

    @Transactional
    @Override
    public void delete(Long id, Long userId) {
        Task task = findTaskById(id);

        Project project = projectPermissionService.getProjectByIdIfCreator(
                task.getProjectId(), userId
        );

        taskRepository.delete(task);

        notifyUser(TaskEventType.TASK_DELETED, project, task);
    }

    @Transactional
    @Override
    public TaskDto assignLabelToTask(Long taskId, Long labelId, Long userId) {
        Task task = findTaskById(taskId);
        Project project = projectPermissionService.getProjectByIdIfCreator(
                task.getProjectId(), userId
        );

        Label label = labelPermissionService.findLabelIfCreator(labelId, userId);

        ensureTaskDoesNotHaveSuchLabel(task.getLabels(), label);
        task.getLabels().add(label);
        Task savedTask = taskRepository.save(task);

        notifyUser(TaskEventType.LABEL_ASSIGNED_TO_TASK, project, savedTask);

        return taskMapper.toDto(savedTask);
    }

    @Transactional
    @Override
    public TaskDto removeLabelFromTask(Long taskId, Long labelId, Long userId) {
        Task task = findTaskById(taskId);
        Project project = projectPermissionService.getProjectByIdIfCreator(
                task.getProjectId(), userId
        );

        Label label = findLabelByIdAndCreator(labelId, userId);

        if (!task.getLabels().remove(label)) {
            throw new EntityNotFoundException(
                    "Task with id: " + taskId + " is not marked with label by id: " + labelId
            );
        }
        Task savedTask = taskRepository.save(task);

        notifyUser(TaskEventType.LABEL_REMOVED_FROM_TASK, project, savedTask);

        return taskMapper.toDto(savedTask);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<TaskDto> search(
            TaskSearchParameters taskSearchParameters,
            Pageable pageable,
            Long userId
    ) {
        TaskSearchParametersWithAssigneeId updatedParams =
                taskMapper.toParamsWithAssignee(taskSearchParameters);
        updatedParams.setAssigneeId(userId);

        Specification<Task> specification = specificationBuilder.build(updatedParams);

        return taskRepository.findAll(specification, pageable)
                .map(taskMapper::toDto);
    }

    private void ensureTaskDoesNotHaveSuchLabel(Set<Label> labels, Label label) {
        if (labels.contains(label)) {
            throw new EntityAlreadyExistsException(
                    "Task is already marked with label by id: " + label.getId()
            );
        }
    }

    private Task findTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("There is no task by id: " + id)
        );
    }

    private Label findLabelByIdAndCreator(Long labelId, Long userId) {
        return labelRepository.findByIdAndCreatorId(labelId, userId).orElseThrow(
                () -> new ForbiddenAccessException(
                        "You do not have permission to access label with id: " + labelId
                )
        );
    }

    private void notifyUser(TaskEventType type, Project project, Task task) {
        publisher.publishEvent(
                taskEventFactory.create(type, project, task)
        );
    }
}
