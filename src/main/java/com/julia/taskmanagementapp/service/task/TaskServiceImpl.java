package com.julia.taskmanagementapp.service.task;

import com.julia.taskmanagementapp.dto.task.CreateTaskRequestDto;
import com.julia.taskmanagementapp.dto.task.TaskDto;
import com.julia.taskmanagementapp.dto.task.UpdateTaskRequestDto;
import com.julia.taskmanagementapp.exception.EntityAlreadyExistsException;
import com.julia.taskmanagementapp.exception.EntityNotFoundException;
import com.julia.taskmanagementapp.exception.ForbiddenAccessException;
import com.julia.taskmanagementapp.mapper.TaskMapper;
import com.julia.taskmanagementapp.model.Label;
import com.julia.taskmanagementapp.model.Task;
import com.julia.taskmanagementapp.repository.LabelRepository;
import com.julia.taskmanagementapp.repository.TaskRepository;
import com.julia.taskmanagementapp.service.project.ProjectPermissionService;
import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskMapper taskMapper;
    private final TaskRepository taskRepository;
    private final LabelRepository labelRepository;
    private final ProjectPermissionService projectPermissionService;

    @Override
    public TaskDto create(CreateTaskRequestDto requestDto, Long userId) {
        projectPermissionService.checkProjectIfCreator(
                requestDto.projectId(), userId
        );
        projectPermissionService.checkProjectIfCollaborator(
                requestDto.projectId(), requestDto.assigneeId()
        );
        Task task = taskMapper.toModel(requestDto);
        task.setStatus(Task.Status.NOT_STARTED);
        Task savedTask = taskRepository.save(task);
        return taskMapper.toDto(savedTask);
    }

    @Override
    public Page<TaskDto> getTasksForProject(Long projectId, Long userId, Pageable pageable) {
        projectPermissionService.checkProjectIfCreatorOrCollaborator(
                projectId, userId
        );
        return taskRepository.findByProjectId(projectId, pageable)
                .map(taskMapper::toDto);
    }

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
        projectPermissionService.checkProjectIfCreator(id, userId);
        Task task = findTaskById(id);
        taskMapper.updateTask(task, requestDto);
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public void delete(Long id, Long userId) {
        projectPermissionService.checkProjectIfCreator(id, userId);
        Task task = findTaskById(id);
        taskRepository.delete(task);
    }

    @Transactional
    @Override
    public TaskDto assignLabelToTask(Long taskId, Long labelId, Long userId) {
        Task task = findTaskById(taskId);
        projectPermissionService.checkProjectIfCreator(task.getProjectId(), userId);

        Label label = findLabelByIdAndCreator(labelId, userId);

        ensureTaskDoesNotHaveSuchLabel(task.getLabels(), label);
        task.getLabels().add(label);
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Transactional
    @Override
    public TaskDto removeLabelFromTask(Long taskId, Long labelId, Long userId) {
        Task task = findTaskById(taskId);
        projectPermissionService.checkProjectIfCreator(task.getProjectId(),userId);

        Label label = findLabelByIdAndCreator(labelId, userId);

        if (!task.getLabels().remove(label)) {
            throw new EntityNotFoundException(
                    "Task with id: " + taskId + " is not marked with label by id: " + labelId
            );
        }
        return taskMapper.toDto(taskRepository.save(task));
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
}
