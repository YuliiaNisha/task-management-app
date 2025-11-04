package com.julia.taskmanagementapp.repository;

import com.julia.taskmanagementapp.model.Label;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelRepository extends JpaRepository<Label, Long> {
    Optional<Label> findByIdAndCreatorId(Long labelId, Long userId);

    boolean existsByNameIgnoreCaseAndCreatorId(String name, Long id);

    boolean existsByColorIgnoreCaseAndCreatorId(String color, Long id);

    Page<Label> findAllByCreatorId(Long userId, Pageable pageable);

    long countByIdInAndCreatorId(Set<Long> ids, Long creatorId);
}
