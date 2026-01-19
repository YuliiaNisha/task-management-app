package com.julia.taskmanagementapp.repository;

import com.julia.taskmanagementapp.model.Task;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    @EntityGraph(attributePaths = "labels")
    Optional<Task> findById(Long id);

    @EntityGraph(attributePaths = "labels")
    Page<Task> findByProjectId(Long projectId, Pageable pageable);

    @EntityGraph(attributePaths = "labels")
    List<Task> findByAssigneeId(Long userId);

    @EntityGraph(attributePaths = "labels")
    Page<Task> findAll(Specification<Task> specification, Pageable pageable);
}
