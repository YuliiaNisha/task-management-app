package com.julia.taskmanagementapp.repository.project.spec;

import com.julia.taskmanagementapp.model.Project;
import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.repository.SpecificationProvider;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class CreatorCollabSpecificationProvider implements SpecificationProvider<Project> {
    @Override
    public String getKey() {
        return Project.SpecificationKey.CREATOR_COLLABORATOR.getValue();
    }

    @Override
    public Specification<Project> getSpecification(String param) {
        return new Specification<Project>() {
            @Override
            public Predicate toPredicate(
                    Root<Project> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder
            ) {
                Long userId = Long.valueOf(param);
                Join<Project, User> collabJoin = root.join("collaborators", JoinType.LEFT);
                query.distinct(true);

                return criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("creator").get("id"), userId),
                        criteriaBuilder.equal(collabJoin.get("id"), userId)
                );
            }
        };

    }
}
