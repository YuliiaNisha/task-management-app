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
public class ProjectStatusSpecificationProvider implements SpecificationProvider<Project> {
    private static final String STATUS_KEY = Project.SpecificationKey.STATUS.getValue();

    @Override
    public String getKey() {
        return STATUS_KEY;
    }

    @Override
    public Specification<Project> getSpecification(String param) {
        return new Specification<Project>() {
            @Override
            public Predicate toPredicate(
                    Root<Project> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get(STATUS_KEY)),
                        param.toLowerCase()
                );
            }
        };
    }
}
