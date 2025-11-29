package com.julia.taskmanagementapp.repository.project;

import com.julia.taskmanagementapp.dto.project.ProjectSearchParametersWithUserId;
import com.julia.taskmanagementapp.model.Project;
import com.julia.taskmanagementapp.repository.SpecificationBuilder;
import com.julia.taskmanagementapp.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProjectSpecificationBuilder
        implements SpecificationBuilder<Project, ProjectSearchParametersWithUserId> {
    private final SpecificationProviderManager<Project> specificationProviderManager;

    @Override
    public Specification<Project> build(ProjectSearchParametersWithUserId searchParameters) {
        if (searchParameters == null) {
            throw new IllegalArgumentException("Search Parameters cannot be null");
        }
        Specification<Project> specification = Specification.unrestricted();
        specification = getSpecificationForParam(
                searchParameters.getName(),
                Project.SpecificationKey.NAME.getValue(), specification
        );
        specification = getSpecificationForParam(
                searchParameters.getStatus(),
                Project.SpecificationKey.STATUS.getValue(), specification
        );
        if (searchParameters.getEndDate() != null) {
            specification = getSpecificationForParam(
                    searchParameters.getEndDate().toString(),
                    Project.SpecificationKey.END_DATE.getValue(), specification
            );
        }
        specification = getSpecificationForParam(
                searchParameters.getUserId().toString(),
                Project.SpecificationKey.CREATOR_COLLABORATOR.getValue(), specification
        );
        return specification;
    }

    private Specification<Project> getSpecificationForParam(
            String searchParameter,
            String key,
            Specification<Project> specification
    ) {
        if (searchParameter != null && !searchParameter.isEmpty()) {
            return specification.and(
                    specificationProviderManager.getSpecificationProvider(key)
                            .getSpecification(searchParameter)
            );
        }
        return specification;
    }
}
