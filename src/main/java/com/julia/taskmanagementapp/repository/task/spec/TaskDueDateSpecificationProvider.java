package com.julia.taskmanagementapp.repository.task.spec;

import com.julia.taskmanagementapp.model.Task;
import com.julia.taskmanagementapp.repository.SpecificationProvider;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskDueDateSpecificationProvider implements SpecificationProvider<Task> {

    public static final String DUE_DATE_KEY = Task.SpecificationKey.DUE_DATE.getValue();

    @Override
    public String getKey() {
        return DUE_DATE_KEY;
    }

    @Override
    public Specification<Task> getSpecification(String param) {
        return new Specification<Task>() {
            @Override
            public Predicate toPredicate(
                    Root<Task> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                LocalDate localDate = LocalDate.parse(param);
                return criteriaBuilder.lessThanOrEqualTo(
                        root.get(DUE_DATE_KEY),
                        localDate);
            }
        };
    }
}
