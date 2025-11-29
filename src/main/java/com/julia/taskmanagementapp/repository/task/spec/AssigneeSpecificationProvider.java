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
public class AssigneeSpecificationProvider implements SpecificationProvider<Task> {
    private static final String ASSIGNEE_KEY = Task.SpecificationKey.ASSIGNEE_ID.getValue();

    @Override
    public String getKey() {
        return ASSIGNEE_KEY;
    }

    @Override
    public Specification<Task> getSpecification(String param) {
        return new Specification<Task>() {
            @Override
            public Predicate toPredicate(
                    Root<Task> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Long assigneeId = Long.valueOf(param);
                return criteriaBuilder.equal(root.get(ASSIGNEE_KEY), assigneeId);
            }
        };
    }
}
