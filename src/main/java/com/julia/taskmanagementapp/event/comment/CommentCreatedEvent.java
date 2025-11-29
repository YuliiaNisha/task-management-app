package com.julia.taskmanagementapp.event.comment;

import com.julia.taskmanagementapp.notification.template.NotificationEmailNames;
import com.julia.taskmanagementapp.notification.template.NotificationEmailSubjects;
import java.util.Map;

public record CommentCreatedEvent(
        Map<String, String> notifyEmailsSet,
        String commentId,
        String text,
        String projectName,
        String taskName,
        String createdByFirstLastName
) implements CommentEvent {
    @Override
    public String emailTemplateName() {
        return NotificationEmailNames.COMMENT_CREATED.getName();
    }

    @Override
    public String emailSubject() {
        return NotificationEmailSubjects.COMMENT_CREATED.getSubject();
    }

    @Override
    public String exceptionMessage() {
        return "Failed to send comment creation email.";
    }
}
