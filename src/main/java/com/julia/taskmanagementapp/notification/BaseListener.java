package com.julia.taskmanagementapp.notification;

import com.julia.taskmanagementapp.exception.NotificationException;
import com.julia.taskmanagementapp.notification.service.EmailTemplateService;
import com.julia.taskmanagementapp.notification.service.MailService;
import jakarta.mail.MessagingException;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseListener {
    private final MailService mailService;
    private final EmailTemplateService emailTemplateService;

    public void sendEmail(
            String emailTemplateName,
            String emailSubject,
            String exceptionMessage,
            Map<String, String> values,
            String email
    ) {
        String emailTemplate = emailTemplateService.loadTemplate(
                emailTemplateName,
                values
        );

        try {
            mailService.sendHtml(
                    email,
                    emailSubject,
                    emailTemplate
            );
        } catch (MessagingException e) {
            throw new NotificationException(exceptionMessage, e);
        }
    }
}
