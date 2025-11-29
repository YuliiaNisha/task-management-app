package com.julia.taskmanagementapp.repository.task;

import com.julia.taskmanagementapp.dto.task.TaskSearchParametersWithAssigneeId;
import com.julia.taskmanagementapp.model.Task;
import com.julia.taskmanagementapp.repository.SpecificationBuilder;
import com.julia.taskmanagementapp.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskSpecificationBuilder
        implements SpecificationBuilder<Task, TaskSearchParametersWithAssigneeId> {
    private final SpecificationProviderManager<Task> specificationProviderManager;

    @Override
    public Specification<Task> build(TaskSearchParametersWithAssigneeId searchParameters) {
        if (searchParameters == null) {
            throw new IllegalArgumentException("Search Parameters cannot be null");
        }
        Specification<Task> specification = Specification.unrestricted();
        specification = getSpecificationForParam(
                searchParameters.getName(),
                Task.SpecificationKey.NAME.getValue(), specification
        );
        specification = getSpecificationForParam(
                searchParameters.getPriority(),
                Task.SpecificationKey.PRIORITY.getValue(), specification
        );
        specification = getSpecificationForParam(
                searchParameters.getStatus(),
                Task.SpecificationKey.STATUS.getValue(), specification
        );
        specification = getSpecificationForParam(
                searchParameters.getProjectId(),
                Task.SpecificationKey.PROJECT_ID.getValue(), specification
        );
        specification = getSpecificationForParam(
                searchParameters.getAssigneeId(),
                Task.SpecificationKey.ASSIGNEE_ID.getValue(), specification
        );
        if (searchParameters.getDueDate() != null) {
            specification = getSpecificationForParam(
                    searchParameters.getDueDate().toString(),
                    Task.SpecificationKey.DUE_DATE.getValue(), specification
            );
        }
        return specification;
    }

    private Specification<Task> getSpecificationForParam(
            String searchParameter,
            String key,
            Specification<Task> specification) {
        if (searchParameter != null && !searchParameter.isEmpty()) {
            return specification.and(
                    specificationProviderManager.getSpecificationProvider(key)
                            .getSpecification(searchParameter)
            );
        }
        return specification;
    }

    private Specification<Task> getSpecificationForParam(
            Long searchParameter,
            String key,
            Specification<Task> specification) {
        if (searchParameter != null) {
            return specification.and(
                    specificationProviderManager.getSpecificationProvider(key)
                            .getSpecification(searchParameter.toString())
            );
        }
        return specification;
    }
}
