package com.julia.taskmanagementapp.mapper;

import com.julia.taskmanagementapp.model.Label;
import com.julia.taskmanagementapp.repository.LabelRepository;
import java.util.HashSet;
import java.util.List;
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
        return new HashSet<>(labelRepository.findAllById(labelIds));
    }

    @Named("getIdsFromLabels")
    public Set<Long> getIdsFromLabels(Set<Label> labels) {
        return labels.stream()
                .map(Label::getId)
                .collect(Collectors.toSet());
    }
}
