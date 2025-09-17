package com.julia.taskmanagementapp.service;

import com.julia.taskmanagementapp.dto.CreateTaskRequestDto;
import com.julia.taskmanagementapp.dto.TaskDto;
import com.julia.taskmanagementapp.dto.UpdateTaskRequestDto;
import com.julia.taskmanagementapp.exception.EntityNotFoundException;
import com.julia.taskmanagementapp.mapper.TaskMapper;
import com.julia.taskmanagementapp.model.Task;
import com.julia.taskmanagementapp.repository.ProjectRepository;
import com.julia.taskmanagementapp.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskMapper taskMapper;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    @Override
    public TaskDto create(CreateTaskRequestDto requestDto) {
        Task task = taskMapper.toModel(requestDto);
        task.setStatus(Task.Status.NOT_STARTED);
        Task savedTask = taskRepository.save(task);
        return taskMapper.toDto(savedTask);
    }

    @Override
    public Page<TaskDto> getTasksForProject(Long projectId, Pageable pageable) {
        projectExists(projectId);
        return taskRepository.findByProjectId(projectId, pageable)
                .map(taskMapper::toDto);
    }

    @Override
    public TaskDto getTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("There is no task by id: " + id)
        );
        return taskMapper.toDto(task);
    }

    @Override
    public TaskDto update(Long id, UpdateTaskRequestDto requestDto) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("There is no task by id: " + id)
        );
        taskMapper.updateTask(task, requestDto);
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public void delete(Long id) {
        taskRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("There is no task by id: " + id)
        );
        taskRepository.deleteById(id);
    }

    private void projectExists(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("There is no project by id: " + projectId);
        }
    }
}
