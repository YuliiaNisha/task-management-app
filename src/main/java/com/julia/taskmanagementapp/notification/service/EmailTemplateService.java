package com.julia.taskmanagementapp.notification.service;

import com.julia.taskmanagementapp.exception.EntityNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {
    public static final String TEMPLATE_EXTENSION = ".html";
    @Value("${app.email-templates.path}")
    private String templatesPath;

    public String loadTemplate(String templateName, Map<String, String> values) {
        try (InputStream inputStream = getClass().getResourceAsStream(
                templatesPath + templateName + TEMPLATE_EXTENSION
        )) {
            if (inputStream == null) {
                throw new EntityNotFoundException(
                        "Template not found: " + templateName
                );
            }
            String template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            for (Map.Entry<String, String> entry : values.entrySet()) {
                template = template.replace("${" + entry.getKey() + "}", entry.getValue());
            }

            return template;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load email template: " + templateName, e);
        }
    }
}
