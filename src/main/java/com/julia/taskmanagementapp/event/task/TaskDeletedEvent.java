package com.julia.taskmanagementapp.event.task;

import com.julia.taskmanagementapp.notification.template.NotificationEmailNames;
import com.julia.taskmanagementapp.notification.template.NotificationEmailSubjects;

public record TaskDeletedEvent(
        String email,
        String toUserName,
        String taskName,
        String projectName
) implements TaskEvent {
    @Override
    public String emailTemplateName() {
        return NotificationEmailNames.TASK_DELETED.getName();
    }

    @Override
    public String emailSubject() {
        return NotificationEmailSubjects.TASK_DELETED.getSubject();
    }

    @Override
    public String exceptionMessage() {
        return "Failed to send task deleted email.";
    }
}
