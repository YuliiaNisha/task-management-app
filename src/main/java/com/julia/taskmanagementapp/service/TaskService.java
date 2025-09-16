package com.julia.taskmanagementapp.service;

import com.julia.taskmanagementapp.dto.CreateTaskRequestDto;
import com.julia.taskmanagementapp.dto.TaskDto;
import com.julia.taskmanagementapp.dto.UpdateTaskRequestDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public interface TaskService {
    TaskDto create(CreateTaskRequestDto requestDto);

    Page<TaskDto> getTasksForProject(Long projectId, Pageable pageable);

    TaskDto getTaskById(Long id);

    TaskDto updateTask(Long id, UpdateTaskRequestDto requestDto);

    void delete(Long id);
}
