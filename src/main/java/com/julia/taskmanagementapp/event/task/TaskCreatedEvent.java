package com.julia.taskmanagementapp.event.task;

import com.julia.taskmanagementapp.notification.template.NotificationEmailNames;
import com.julia.taskmanagementapp.notification.template.NotificationEmailSubjects;

public record TaskCreatedEvent(
        String email,
        String toUserName,
        String taskId,
        String taskName,
        String projectName,
        String dueDate
) implements DueDateTaskIdTaskEvent {
    @Override
    public String emailTemplateName() {
        return NotificationEmailNames.TASK_CREATED.getName();

    }

    @Override
    public String emailSubject() {
        return NotificationEmailSubjects.TASK_CREATED.getSubject();
    }

    @Override
    public String exceptionMessage() {
        return "Failed to send task created email.";
    }
}
