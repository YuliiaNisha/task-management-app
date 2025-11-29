package com.julia.taskmanagementapp.notification;

import com.julia.taskmanagementapp.event.comment.CommentCreatedEvent;
import com.julia.taskmanagementapp.event.comment.CommentEvent;
import com.julia.taskmanagementapp.notification.service.EmailTemplateService;
import com.julia.taskmanagementapp.notification.service.MailService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CommentListener extends BaseListener {
    @Value("${app.base-url}")
    private String baseUrl;

    public CommentListener(MailService mailService, EmailTemplateService emailTemplateService) {
        super(mailService, emailTemplateService);
    }

    @EventListener
    public void handleCommentCreated(CommentCreatedEvent event) {
        sendNotification(event);
    }

    private void sendNotification(CommentEvent event) {
        String commentLink = event.commentLink(baseUrl);

        for (Map.Entry<String, String> emailName : event.notifyEmailsSet().entrySet()) {
            Map<String, String> values = event.emailTemplateValues(
                    commentLink, emailName.getValue()
            );
            sendEmail(
                    event.emailTemplateName(),
                    event.emailSubject(),
                    event.exceptionMessage(),
                    values,
                    emailName.getKey()
            );
        }
    }
}
