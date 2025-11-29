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
public class TaskStatusSpecificationProvider implements SpecificationProvider<Task> {

    public static final String STATUS_KEY = Task.SpecificationKey.STATUS.getValue();

    @Override
    public String getKey() {
        return STATUS_KEY;
    }

    @Override
    public Specification<Task> getSpecification(String param) {
        return new Specification<Task>() {
            @Override
            public Predicate toPredicate(
                    Root<Task> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get(STATUS_KEY)),
                        param.toLowerCase());
            }
        };
    }
}
