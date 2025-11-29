package com.julia.taskmanagementapp.notification.template;

public enum NotificationEmailSubjects {
    PROJECT_CREATED("New Project Created"),
    PROJECT_UPDATED("Project is updated"),
    PROJECT_DELETED("Project is deleted"),
    TASK_CREATED("New Task Created"),
    TASK_UPDATED("Task is updated"),
    TASK_DELETED("Task is deleted"),
    LABEL_ASSIGNED_TO_TASK("New label is assigned to task"),
    LABEL_REMOVED_FROM_TASK("Label is removed from task"),
    COMMENT_CREATED("New Comment Created");

    private final String subjectLine;

    NotificationEmailSubjects(String subjectLine) {
        this.subjectLine = subjectLine;
    }

    public String getSubject() {
        return subjectLine;
    }
}
