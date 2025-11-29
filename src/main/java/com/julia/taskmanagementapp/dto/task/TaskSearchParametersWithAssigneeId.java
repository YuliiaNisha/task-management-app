package com.julia.taskmanagementapp.dto.task;

import java.time.LocalDate;
import lombok.Data;

@Data
public class TaskSearchParametersWithAssigneeId {
    private String name;

    private String priority;

    private String status;

    private Long projectId;

    private LocalDate dueDate;

    private Long assigneeId;
}
