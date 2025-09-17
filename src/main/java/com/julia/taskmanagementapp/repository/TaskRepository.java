package com.julia.taskmanagementapp.repository;

import com.julia.taskmanagementapp.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByProjectId(Long projectId, Pageable pageable);
}
