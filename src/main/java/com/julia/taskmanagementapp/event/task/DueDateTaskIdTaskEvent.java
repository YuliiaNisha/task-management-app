package com.julia.taskmanagementapp.event.task;

import java.util.Map;
import org.springframework.web.util.UriComponentsBuilder;

public interface DueDateTaskIdTaskEvent extends TaskEvent {
    String dueDate();

    String taskId();

    default String pathSegment() {
        return "tasks";
    }

    default Map<String, String> emailTemplateValues(String taskLink) {
        return Map.of(
                "toUserName", this.toUserName(),
                "taskName", this.taskName(),
                "projectName", this.projectName(),
                "dueDate", this.dueDate(),
                "taskLink", taskLink
        );
    }

    default String taskLink(String baseUrl) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .pathSegment(pathSegment(), this.taskId())
                .toUriString();
    }
}
