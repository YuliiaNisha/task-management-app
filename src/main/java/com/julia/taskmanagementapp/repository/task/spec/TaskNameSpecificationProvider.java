package com.julia.taskmanagementapp.repository.task.spec;

import com.julia.taskmanagementapp.model.Task;
import com.julia.taskmanagementapp.repository.SpecificationProvider;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskNameSpecificationProvider implements SpecificationProvider<Task> {
    public static final String NAME_KEY = Task.SpecificationKey.NAME.getValue();

    @Override
    public String getKey() {
        return NAME_KEY;
    }

    @Override
    public Specification<Task> getSpecification(String param) {
        return new Specification<Task>() {
            @Override
            public Predicate toPredicate(
                    Root<Task> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(NAME_KEY)),
                        "%" + param.toLowerCase() + "%");
            }
        };
    }
}
