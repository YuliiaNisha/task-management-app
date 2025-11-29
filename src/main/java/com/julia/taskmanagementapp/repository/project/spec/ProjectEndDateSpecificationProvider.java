package com.julia.taskmanagementapp.repository.project.spec;

import com.julia.taskmanagementapp.model.Project;
import com.julia.taskmanagementapp.repository.SpecificationProvider;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ProjectEndDateSpecificationProvider implements SpecificationProvider<Project> {

    public static final String END_DATE_KEY = Project.SpecificationKey.END_DATE.getValue();

    @Override
    public String getKey() {
        return END_DATE_KEY;
    }

    @Override
    public Specification<Project> getSpecification(String param) {
        return new Specification<Project>() {
            @Override
            public Predicate toPredicate(
                    Root<Project> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                LocalDate localDate = LocalDate.parse(param);
                return criteriaBuilder.lessThanOrEqualTo(
                        root.get(END_DATE_KEY),
                        localDate);
            }
        };
    }
}
