package com.julia.taskmanagementapp.event.task;

import java.util.Map;

public interface TaskEvent {
    String email();

    String toUserName();

    String taskName();

    String projectName();

    String emailTemplateName();

    String emailSubject();

    String exceptionMessage();

    default Map<String, String> emailTemplateValues() {
        return Map.of(
                "toUserName", this.toUserName(),
                "taskName", this.taskName(),
                "projectName", this.projectName()
        );
    }
}
