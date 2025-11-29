package com.julia.taskmanagementapp.event.task;

import com.julia.taskmanagementapp.notification.template.NotificationEmailNames;
import com.julia.taskmanagementapp.notification.template.NotificationEmailSubjects;

public record LabelRemovedFromTaskEvent(
        String email,
        String toUserName,
        String taskId,
        String taskName,
        String projectName,
        String dueDate
) implements DueDateTaskIdTaskEvent {
    @Override
    public String emailTemplateName() {
        return NotificationEmailNames.LABEL_REMOVED_FROM_TASK.getName();
    }

    @Override
    public String emailSubject() {
        return NotificationEmailSubjects.LABEL_REMOVED_FROM_TASK.getSubject();
    }

    @Override
    public String exceptionMessage() {
        return "Failed to send label removed from task email.";
    }
}
