package com.julia.taskmanagementapp.mapper;

import com.julia.taskmanagementapp.config.MapperConfig;
import com.julia.taskmanagementapp.dto.CreateTaskRequestDto;
import com.julia.taskmanagementapp.dto.TaskDto;
import com.julia.taskmanagementapp.dto.UpdateTaskRequestDto;
import com.julia.taskmanagementapp.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface TaskMapper {
    Task toModel(CreateTaskRequestDto requestDto);

    TaskDto toDto(Task task);

    void updateTask(@MappingTarget Task task, UpdateTaskRequestDto requestDto);
}
