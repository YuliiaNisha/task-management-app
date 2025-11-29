package com.julia.taskmanagementapp.repository;

import com.julia.taskmanagementapp.model.Project;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository
        extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {
    @Query("SELECT DISTINCT p FROM Project p "
            + "LEFT JOIN p.collaborators c "
            + "WHERE p.creator.id = :userId OR c.id = :userId")
    @EntityGraph(attributePaths = {"creator", "collaborators"})
    Page<Project> findAllByCreatorOrCollaborators(
            @Param("userId") Long userId, Pageable pageable
    );

    @EntityGraph(attributePaths = {"creator", "collaborators"})
    @Query("SELECT p FROM Project p "
            + "WHERE p.id = :projectId AND p.creator.id = :userId")
    Optional<Project> findByIdAndCreator(
            @Param("projectId") Long id, @Param("userId") Long userId
    );

    @EntityGraph(attributePaths = {"creator", "collaborators"})
    @Query("SELECT p FROM Project p "
            + "WHERE p.id = :projectId "
            + "AND (p.creator.id = :userId "
            + "OR EXISTS (SELECT c FROM p.collaborators c WHERE c.id = :userId))")
    Optional<Project> findByIdAndCreatorAndCollaborators(
            @Param("projectId") Long id, @Param("userId") Long userId
    );

    @Query("""
       SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END
       FROM Project p
       LEFT JOIN p.collaborators c
       WHERE p.id = :projectId
         AND (p.creator.id = :userId OR c.id = :userId)""")
    boolean existsByIdAndCreatorAndCollaborators(
            @Param("projectId") Long id, @Param("userId") Long userId
    );

    @Query("""
       SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END
       FROM Project p
       WHERE p.id = :projectId AND p.creator.id = :userId""")
    boolean existsByIdAndCreator(
            @Param("projectId") Long id, @Param("userId") Long userId
    );

    @Query("""
       SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END
       FROM Project p
       JOIN p.collaborators c
       WHERE p.id = :projectId AND c.id = :collaboratorId""")
    boolean existsByIdAndCollaborator(
            @Param("projectId") Long id, @Param("collaboratorId") Long collaboratorId
    );

    @EntityGraph(attributePaths = "collaborators")
    Page<Project> findAll(Specification<Project> specification, Pageable pageable);
}
