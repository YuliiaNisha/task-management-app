package com.julia.taskmanagementapp.repository;

import com.julia.taskmanagementapp.model.Project;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Page<Project> findByIdIn(List<Long> ids, Pageable pageable);
}
