package com.julia.taskmanagementapp.event.comment;

import java.util.Map;
import org.springframework.web.util.UriComponentsBuilder;

public interface CommentEvent {
    Map<String, String> notifyEmailsSet();

    String commentId();

    String text();

    String projectName();

    String taskName();

    String createdByFirstLastName();

    String emailTemplateName();

    String emailSubject();

    String exceptionMessage();

    default String pathSegment() {
        return "comments";
    }

    default Map<String, String> emailTemplateValues(String commentLink, String recipientFirstName) {
        return Map.of(
                "recipientFirstName", recipientFirstName,
                "text", this.text(),
                "createdByFirstLastName", this.createdByFirstLastName(),
                "taskName", this.taskName(),
                "projectName", this.projectName(),
                "commentLink", commentLink
        );
    }

    default String commentLink(String baseUrl) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .pathSegment(pathSegment(), this.commentId())
                .toUriString();
    }
}
