package com.julia.taskmanagementapp.notification;

import com.julia.taskmanagementapp.event.task.DueDateTaskIdTaskEvent;
import com.julia.taskmanagementapp.event.task.LabelAssignedToTaskEvent;
import com.julia.taskmanagementapp.event.task.LabelRemovedFromTaskEvent;
import com.julia.taskmanagementapp.event.task.TaskCreatedEvent;
import com.julia.taskmanagementapp.event.task.TaskDeletedEvent;
import com.julia.taskmanagementapp.event.task.TaskEvent;
import com.julia.taskmanagementapp.event.task.TaskUpdatedEvent;
import com.julia.taskmanagementapp.notification.service.EmailTemplateService;
import com.julia.taskmanagementapp.notification.service.MailService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TaskListener extends BaseListener {
    @Value("${app.base-url}")
    private String baseUrl;

    public TaskListener(MailService mailService, EmailTemplateService emailTemplateService) {
        super(mailService, emailTemplateService);
    }

    @EventListener
    public void handleTaskCreated(TaskCreatedEvent event) {
        sendNotificationWithTaskLink(event);
    }

    @EventListener
    public void handleTaskUpdated(TaskUpdatedEvent event) {
        sendNotificationWithTaskLink(event);
    }

    @EventListener
    public void handleTaskDeleted(TaskDeletedEvent event) {
        sendNotificationWithoutTaskLink(event);
    }

    @EventListener
    public void handleLabelAssignedToTask(LabelAssignedToTaskEvent event) {
        sendNotificationWithTaskLink(event);
    }

    @EventListener
    public void handleLabelRemovedFromTask(LabelRemovedFromTaskEvent event) {
        sendNotificationWithTaskLink(event);
    }

    private void sendNotificationWithTaskLink(DueDateTaskIdTaskEvent event) {
        String taskLink = event.taskLink(baseUrl);
        Map<String, String> values = event.emailTemplateValues(taskLink);

        sendEmail(
                event.emailTemplateName(),
                event.emailSubject(),
                event.exceptionMessage(),
                values,
                event.email()
        );
    }

    private void sendNotificationWithoutTaskLink(TaskEvent event) {
        Map<String, String> values = event.emailTemplateValues();

        sendEmail(
                event.emailTemplateName(),
                event.emailSubject(),
                event.exceptionMessage(),
                values,
                event.email()
        );
    }
}
