package com.julia.taskmanagementapp.notification;

import com.julia.taskmanagementapp.event.project.ProjectCreatedEvent;
import com.julia.taskmanagementapp.event.project.ProjectDeletedEvent;
import com.julia.taskmanagementapp.event.project.ProjectEvent;
import com.julia.taskmanagementapp.event.project.ProjectUpdatedEvent;
import com.julia.taskmanagementapp.notification.service.EmailTemplateService;
import com.julia.taskmanagementapp.notification.service.MailService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ProjectListener extends BaseListener {
    @Value("${app.base-url}")
    private String baseUrl;

    public ProjectListener(MailService mailService, EmailTemplateService emailTemplateService) {
        super(mailService, emailTemplateService);
    }

    @EventListener
    public void handleProjectCreated(ProjectCreatedEvent event) {
        sendNotification(event);
    }

    @EventListener
    public void handleProjectUpdated(ProjectUpdatedEvent event) {
        sendNotification(event);
    }

    @EventListener
    public void handleProjectDeleted(ProjectDeletedEvent event) {
        sendNotification(event);
    }

    private void sendNotification(ProjectEvent event) {
        String projectLink = event.projectLink(baseUrl);

        for (Map.Entry<String, String> emailCollaboratorName
                : event.emailsCollaboratorNames().entrySet()) {
            Map<String, String> values = event.emailTemplateValues(
                    projectLink, emailCollaboratorName.getValue()
            );

            sendEmail(
                    event.emailTemplateName(),
                    event.emailSubject(),
                    event.exceptionMessage(),
                    values,
                    emailCollaboratorName.getKey()
            );
        }
    }
}
