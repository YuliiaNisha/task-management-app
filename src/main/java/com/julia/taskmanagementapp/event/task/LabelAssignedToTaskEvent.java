package com.julia.taskmanagementapp.event.task;

import com.julia.taskmanagementapp.notification.template.NotificationEmailNames;
import com.julia.taskmanagementapp.notification.template.NotificationEmailSubjects;

public record LabelAssignedToTaskEvent(
        String email,
        String toUserName,
        String taskId,
        String taskName,
        String projectName,
        String dueDate
) implements DueDateTaskIdTaskEvent {
    @Override
    public String emailTemplateName() {
        return NotificationEmailNames.LABEL_ASSIGNED_TO_TASK.getName();
    }

    @Override
    public String emailSubject() {
        return NotificationEmailSubjects.LABEL_ASSIGNED_TO_TASK.getSubject();
    }

    @Override
    public String exceptionMessage() {
        return "Failed to send label assigned to task email.";
    }
}
