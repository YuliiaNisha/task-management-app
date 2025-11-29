package com.julia.taskmanagementapp.repository;

import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<S, T> {
    Specification<S> build(T searchParameters);
}
