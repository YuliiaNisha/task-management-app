package com.julia.taskmanagementapp.service.task;

import com.julia.taskmanagementapp.dto.task.CreateTaskRequestDto;
import com.julia.taskmanagementapp.dto.task.TaskDto;
import com.julia.taskmanagementapp.dto.task.UpdateTaskRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    TaskDto create(CreateTaskRequestDto requestDto, Long userId);

    Page<TaskDto> getTasksForProject(Long projectId, Long userId, Pageable pageable);

    TaskDto getTaskById(Long id, Long userId);

    TaskDto update(Long id, UpdateTaskRequestDto requestDto, Long userId);

    void delete(Long id, Long userId);
}
