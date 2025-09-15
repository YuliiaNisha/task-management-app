package com.julia.taskmanagementapp.service;

import com.julia.taskmanagementapp.dto.CreateTaskRequestDto;
import com.julia.taskmanagementapp.dto.TaskDto;

public interface TaskService {
    TaskDto create(CreateTaskRequestDto requestDto);
}
