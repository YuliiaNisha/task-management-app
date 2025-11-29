package com.julia.taskmanagementapp.event.project;

import com.julia.taskmanagementapp.notification.template.NotificationEmailNames;
import com.julia.taskmanagementapp.notification.template.NotificationEmailSubjects;
import java.util.Map;

public record ProjectCreatedEvent(
        Map<String, String> emailsCollaboratorNames,
        String projectName,
        String creatorFirstLastName,
        String endDate,
        String projectId
) implements ProjectEvent {
    @Override
    public String emailTemplateName() {
        return NotificationEmailNames.PROJECT_CREATED.getName();

    }

    @Override
    public String emailSubject() {
        return NotificationEmailSubjects.PROJECT_CREATED.getSubject();
    }

    @Override
    public String exceptionMessage() {
        return "Failed to send project created email.";
    }
}
