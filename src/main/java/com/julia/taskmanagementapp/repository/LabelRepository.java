package com.julia.taskmanagementapp.repository;

import com.julia.taskmanagementapp.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelRepository extends JpaRepository<Label, Long> {
    boolean existsByNameIgnoreCase(String name);

    boolean existsByColorIgnoreCase(String color);
}
