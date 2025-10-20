package com.julia.taskmanagementapp.service.project;

import com.julia.taskmanagementapp.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectPermissionService {
    Page<Project> getProjectsIfCreatorOrCollaborator(Long userId, Pageable pageable);

    Project getProjectByIdIfCreatorOrCollaborator(Long projectId, Long userId);

    Project getProjectByIdIfCreator(Long projectId, Long userId);
}
