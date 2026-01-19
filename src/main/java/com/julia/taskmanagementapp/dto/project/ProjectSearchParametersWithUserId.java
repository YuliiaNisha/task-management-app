package com.julia.taskmanagementapp.dto.project;

import java.time.LocalDate;
import lombok.Data;

@Data
public class ProjectSearchParametersWithUserId {
    private String name;
    private LocalDate endDate;
    private String status;
    private Long userId;
}
