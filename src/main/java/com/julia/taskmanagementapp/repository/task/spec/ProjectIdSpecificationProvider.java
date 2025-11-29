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
public class ProjectIdSpecificationProvider implements SpecificationProvider<Task> {

    public static final String PROJECT_ID_KEY = Task.SpecificationKey.PROJECT_ID.getValue();

    @Override
    public String getKey() {
        return PROJECT_ID_KEY;
    }

    @Override
    public Specification<Task> getSpecification(String param) {
        return new Specification<Task>() {
            @Override
            public Predicate toPredicate(
                    Root<Task> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Long projectId = Long.valueOf(param);
                return criteriaBuilder.equal(root.get(PROJECT_ID_KEY), projectId);
            }
        };
    }
}
