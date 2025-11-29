package com.julia.taskmanagementapp.repository.project;

import com.julia.taskmanagementapp.exception.SpecificationProviderNotFoundException;
import com.julia.taskmanagementapp.model.Project;
import com.julia.taskmanagementapp.repository.SpecificationProvider;
import com.julia.taskmanagementapp.repository.SpecificationProviderManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProjectSpecificationProviderManager implements SpecificationProviderManager<Project> {
    private final List<SpecificationProvider<Project>> specificationProvidersList;

    @Override
    public SpecificationProvider<Project> getSpecificationProvider(String key) {
        return specificationProvidersList.stream()
                .filter(provider -> provider.getKey().equals(key))
                .findFirst()
                .orElseThrow(
                        () -> new SpecificationProviderNotFoundException(
                                "Can't find SpecificationProvider for key: "
                                        + key)
                );
    }
}
