package com.julia.taskmanagementapp.repository;

import com.julia.taskmanagementapp.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
