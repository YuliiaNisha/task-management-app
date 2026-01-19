package com.julia.taskmanagementapp.event.project.factory;

import com.julia.taskmanagementapp.event.project.ProjectCreatedEvent;
import com.julia.taskmanagementapp.event.project.ProjectDeletedEvent;
import com.julia.taskmanagementapp.event.project.ProjectEvent;
import com.julia.taskmanagementapp.event.project.ProjectUpdatedEvent;
import com.julia.taskmanagementapp.exception.EntityNotFoundException;
import com.julia.taskmanagementapp.model.Project;
import com.julia.taskmanagementapp.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.stereotype.Service;

@Service
public class ProjectEventFactory {
    private final Map<ProjectEventType, Function<Project, ProjectEvent>> registry =
            Map.of(
                    ProjectEventType.PROJECT_CREATED, this::createProjectCreatedEvent,
                    ProjectEventType.PROJECT_UPDATED, this::createProjectUpdatedEvent,
                    ProjectEventType.PROJECT_DELETED, this::createProjectDeletedEvent
            );

    public ProjectEvent create(ProjectEventType type, Project project) {
        Function<Project, ProjectEvent> function = registry.get(type);

        if (function == null) {
            throw new EntityNotFoundException(
                    "There is no function by name: " + type.name()
            );
        }

        return function.apply(project);
    }

    private Map<String, String> getEmailsCollaboratorNames(Project project) {
        Map<String, String> emailsCollaboratorNames = new HashMap<>();
        for (User collaborator : project.getCollaborators()) {
            emailsCollaboratorNames.put(
                    collaborator.getEmail(), collaborator.getFirstName()
            );
        }
        return emailsCollaboratorNames;
    }

    private ProjectDeletedEvent createProjectDeletedEvent(Project project) {
        return new ProjectDeletedEvent(
                getEmailsCollaboratorNames(project),
                project.getName(),
                String.join(
                        " ",
                        project.getCreator().getFirstName(),
                        project.getCreator().getLastName()
                ),
                project.getEndDate().toString(),
                project.getId().toString()
        );
    }

    private ProjectUpdatedEvent createProjectUpdatedEvent(Project project) {
        return new ProjectUpdatedEvent(
                getEmailsCollaboratorNames(project),
                project.getName(),
                String.join(
                        " ",
                        project.getCreator().getFirstName(),
                        project.getCreator().getLastName()
                ),
                project.getEndDate().toString(),
                project.getId().toString()
        );
    }

    private ProjectCreatedEvent createProjectCreatedEvent(Project project) {
        return new ProjectCreatedEvent(
                getEmailsCollaboratorNames(project),
                project.getName(),
                String.join(
                        " ",
                        project.getCreator().getFirstName(),
                        project.getCreator().getLastName()
                ),
                project.getEndDate().toString(),
                project.getId().toString()
        );
    }
}
