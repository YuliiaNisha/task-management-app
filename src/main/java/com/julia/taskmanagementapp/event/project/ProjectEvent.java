package com.julia.taskmanagementapp.event.project;

import java.util.Map;
import org.springframework.web.util.UriComponentsBuilder;

public interface ProjectEvent {
    Map<String, String> emailsCollaboratorNames();

    String projectName();

    String creatorFirstLastName();

    String endDate();

    String projectId();

    String emailTemplateName();

    String emailSubject();

    String exceptionMessage();

    default String pathSegment() {
        return "projects";
    }

    default Map<String, String> emailTemplateValues(
            String projectLink, String collaboratorFirstName
    ) {
        return Map.of(
                "projectCollaboratorFirstName", collaboratorFirstName,
                "projectName", this.projectName(),
                "creatorFirstLastName", this.creatorFirstLastName(),
                "endDate", this.endDate(),
                "projectLink", projectLink
        );
    }

    default String projectLink(String baseUrl) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .pathSegment(pathSegment(), this.projectId())
                .toUriString();
    }
}
