package com.julia.taskmanagementapp.service.label;

import com.julia.taskmanagementapp.model.Label;
import java.util.Optional;
import java.util.Set;

public interface LabelPermissionService {
    void checkLabelsIfCreator (Set<Long> labelIds, Long userId);

    Label findLabelIfCreator (Long labelId, Long userId);
}
