package com.julia.taskmanagementapp.service.task;

import com.julia.taskmanagementapp.dto.task.CreateTaskRequestDto;
import com.julia.taskmanagementapp.dto.task.TaskDto;
import com.julia.taskmanagementapp.dto.task.UpdateTaskRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    TaskDto create(CreateTaskRequestDto requestDto);

    Page<TaskDto> getTasksForProject(Long projectId, Pageable pageable);

    TaskDto getTaskById(Long id);

    TaskDto update(Long id, UpdateTaskRequestDto requestDto);

    void delete(Long id);
}
