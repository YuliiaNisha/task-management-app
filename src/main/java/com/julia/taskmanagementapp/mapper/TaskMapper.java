package com.julia.taskmanagementapp.mapper;

import com.julia.taskmanagementapp.config.MapperConfig;
import com.julia.taskmanagementapp.dto.task.CreateTaskRequestDto;
import com.julia.taskmanagementapp.dto.task.TaskDto;
import com.julia.taskmanagementapp.dto.task.UpdateTaskRequestDto;
import com.julia.taskmanagementapp.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = LabelsProvider.class)
public interface TaskMapper {
    @Mapping(source = "labelIds", target = "labels", qualifiedByName = "getLabelsFromIds")
    Task toModel(CreateTaskRequestDto requestDto);

    @Mapping(source = "labels", target = "labelIds", qualifiedByName = "getIdsFromLabels")
    TaskDto toDto(Task task);

    @Mapping(source = "labelIds", target = "labels", qualifiedByName = "getLabelsFromIds")
    void updateTask(@MappingTarget Task task, UpdateTaskRequestDto requestDto);
}
