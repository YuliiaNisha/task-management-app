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
public class TaskPrioritySpecificationProvider implements SpecificationProvider<Task> {

    public static final String PRIORITY_KEY = Task.SpecificationKey.PRIORITY.getValue();

    @Override
    public String getKey() {
        return PRIORITY_KEY;
    }

    @Override
    public Specification<Task> getSpecification(String param) {
        return new Specification<Task>() {
            @Override
            public Predicate toPredicate(
                    Root<Task> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get(PRIORITY_KEY)),
                        param.toLowerCase());
            }
        };
    }
}
