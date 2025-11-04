package com.julia.taskmanagementapp.service.label;

import com.julia.taskmanagementapp.exception.ForbiddenAccessException;
import com.julia.taskmanagementapp.model.Label;
import com.julia.taskmanagementapp.repository.LabelRepository;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LabelPermissionServiceImpl implements LabelPermissionService {
    private final LabelRepository labelRepository;

    @Override
    public void checkLabelsIfCreator(Set<Long> labelIds, Long userId) {
        long count = labelRepository.countByIdInAndCreatorId(labelIds, userId);
        if (count != labelIds.size()) {
            throw new ForbiddenAccessException(
                    "You have no permission to use one or more of the provided labels."
            );
        }
    }

    @Override
    public Label findLabelIfCreator(Long labelId, Long userId) {
        Optional<Label> labelOptional = labelRepository.findByIdAndCreatorId(labelId, userId);
        if (labelOptional.isEmpty()) {
            throw new ForbiddenAccessException(
                    "You have no permission to use labels by id: " + labelId
            );
        }
        return labelOptional.get();
    }


}
