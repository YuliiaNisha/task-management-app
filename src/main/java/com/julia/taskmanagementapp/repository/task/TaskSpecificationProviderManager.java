package com.julia.taskmanagementapp.repository.task;

import com.julia.taskmanagementapp.exception.SpecificationProviderNotFoundException;
import com.julia.taskmanagementapp.model.Task;
import com.julia.taskmanagementapp.repository.SpecificationProvider;
import com.julia.taskmanagementapp.repository.SpecificationProviderManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TaskSpecificationProviderManager implements SpecificationProviderManager<Task> {
    private final List<SpecificationProvider<Task>> specificationProvidersList;

    @Override
    public SpecificationProvider<Task> getSpecificationProvider(String key) {
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
