package com.julia.taskmanagementapp.service.label;

import com.julia.taskmanagementapp.dto.label.CreateLabelRequestDto;
import com.julia.taskmanagementapp.dto.label.LabelDto;

public interface LabelService {
    LabelDto create(CreateLabelRequestDto requestDto);
}
