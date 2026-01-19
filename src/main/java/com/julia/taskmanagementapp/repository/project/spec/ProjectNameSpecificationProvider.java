package com.julia.taskmanagementapp.repository.project.spec;

import com.julia.taskmanagementapp.model.Project;
import com.julia.taskmanagementapp.repository.SpecificationProvider;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ProjectNameSpecificationProvider implements SpecificationProvider<Project> {
    public static final String NAME_KEY = Project.SpecificationKey.NAME.getValue();

    @Override
    public String getKey() {
        return NAME_KEY;
    }

    @Override
    public Specification<Project> getSpecification(String param) {
        return new Specification<Project>() {
            @Override
            public Predicate toPredicate(
                    Root<Project> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(NAME_KEY)),
                        "%" + param.toLowerCase() + "%");
            }
        };
    }
}
