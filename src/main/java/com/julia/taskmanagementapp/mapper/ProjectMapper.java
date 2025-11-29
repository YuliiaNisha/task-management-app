package com.julia.taskmanagementapp.mapper;

import com.julia.taskmanagementapp.config.MapperConfig;
import com.julia.taskmanagementapp.dto.project.CreateProjectRequestDto;
import com.julia.taskmanagementapp.dto.project.ProjectDto;
import com.julia.taskmanagementapp.dto.project.ProjectSearchParameters;
import com.julia.taskmanagementapp.dto.project.ProjectSearchParametersWithUserId;
import com.julia.taskmanagementapp.dto.project.UpdateProjectRequestDto;
import com.julia.taskmanagementapp.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = UsersProvider.class)
public interface ProjectMapper {
    @Mapping(target = "collaborators", ignore = true)
    Project toModel(CreateProjectRequestDto requestDto);

    @Mapping(
            source = "collaborators",
            target = "collaboratorIds",
            qualifiedByName = "getIdsFromUsers"
    )
    ProjectDto toDto(Project project);

    @Mapping(target = "collaborators", ignore = true)
    void update(@MappingTarget Project project, UpdateProjectRequestDto requestDto);

    ProjectSearchParametersWithUserId toParamsWithUserId(ProjectSearchParameters searchParameters);
}
