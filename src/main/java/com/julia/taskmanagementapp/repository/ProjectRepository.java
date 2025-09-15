package com.julia.taskmanagementapp.repository;

import com.julia.taskmanagementapp.model.Project;
import com.julia.taskmanagementapp.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
