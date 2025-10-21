package com.julia.taskmanagementapp.service.project;

import com.julia.taskmanagementapp.exception.ForbiddenAccessException;
import com.julia.taskmanagementapp.model.Project;
import com.julia.taskmanagementapp.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProjectPermissionServiceImpl implements ProjectPermissionService {
    private final ProjectRepository projectRepository;

    @Override
    public Page<Project> getProjectsIfCreatorOrCollaborator(Long userId, Pageable pageable) {
        return projectRepository.findAllByCreatorOrCollaborators(userId, pageable);
    }

    @Override
    public Project getProjectByIdIfCreatorOrCollaborator(Long projectId, Long userId) {
        return projectRepository.findByIdAndCreatorAndCollaborators(
                projectId, userId
        ).orElseThrow(
                () -> new ForbiddenAccessException(
                        "You do not have permission to view project with id: " + projectId
                )
        );
    }

    @Override
    public void checkProjectIfCreatorOrCollaborator(Long projectId, Long userId) {
        if (!projectRepository.existsByIdAndCreatorAndCollaborators(projectId, userId)) {
            throw new ForbiddenAccessException(
                    "You do not have permission to access project with id: " + projectId
            );
        }
    }

    @Override
    public void checkProjectIfCollaborator(Long projectId, Long userId) {
        if (!projectRepository.existsByIdAndCollaborator(projectId, userId)) {
            throw new ForbiddenAccessException(
                    "User with id: " + userId
                    + " is not a collaborator of the project with id: " + projectId
            );
        }
    }

    @Override
    public void checkProjectIfCreator(Long projectId, Long userId) {
        if (!projectRepository.existsByIdAndCreator(projectId, userId)) {
            throw new ForbiddenAccessException(
                    "You do not have permission to access project with id: " + projectId
            );
        }
    }

    @Override
    public Project getProjectByIdIfCreator(Long projectId, Long userId) {
        return projectRepository.findByIdAndCreator(projectId, userId)
                .orElseThrow(
                        () -> new ForbiddenAccessException(
                                "You do not have permission to view project with id: " + projectId
                        )
                );
        }
}
