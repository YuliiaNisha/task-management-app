package com.julia.taskmanagementapp.service.label;

import com.julia.taskmanagementapp.dto.label.CreateLabelRequestDto;
import com.julia.taskmanagementapp.dto.label.LabelDto;
import com.julia.taskmanagementapp.dto.label.UpdateLabelRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LabelService {
    LabelDto create(CreateLabelRequestDto requestDto, Long userId);

    Page<LabelDto> getLabels(Long userId, Pageable pageable);

    LabelDto update(Long id, UpdateLabelRequestDto requestDto, Long userId);

    void delete(Long id, Long userId);
}
