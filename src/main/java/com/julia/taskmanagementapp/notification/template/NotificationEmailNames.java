package com.julia.taskmanagementapp.notification.template;

public enum NotificationEmailNames {
    PROJECT_CREATED("projectCreated"),
    PROJECT_UPDATED("projectUpdated"),
    PROJECT_DELETED("projectDeleted"),
    TASK_CREATED("taskCreated"),
    TASK_UPDATED("taskUpdated"),
    TASK_DELETED("taskDeleted"),
    LABEL_ASSIGNED_TO_TASK("labelAssignedToTask"),
    LABEL_REMOVED_FROM_TASK("labelRemovedFromTask"),
    COMMENT_CREATED("commentCreated");

    private final String fileName;

    NotificationEmailNames(String fileName) {
        this.fileName = fileName;
    }

    public String getName() {
        return fileName;
    }
}
