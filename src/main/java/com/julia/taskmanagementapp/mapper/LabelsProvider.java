package com.julia.taskmanagementapp.mapper;

import com.julia.taskmanagementapp.model.Label;
import com.julia.taskmanagementapp.repository.LabelRepository;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LabelsProvider {
    private final LabelRepository labelRepository;

    @Named("getLabelsFromIds")
    public Set<Label> getLabelsFromIds(Set<Long> labelIds) {
        if (labelIds != null) {
            return new HashSet<>(labelRepository.findAllById(labelIds));
        }
        return new HashSet<>();
    }

    @Named("getIdsFromLabels")
    public Set<Long> getIdsFromLabels(Set<Label> labels) {
        return labels.stream()
                .map(Label::getId)
                .collect(Collectors.toSet());
    }
}
