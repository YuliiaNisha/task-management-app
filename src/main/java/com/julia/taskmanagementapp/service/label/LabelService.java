package com.julia.taskmanagementapp.service.label;

import com.julia.taskmanagementapp.dto.label.CreateLabelRequestDto;
import com.julia.taskmanagementapp.dto.label.LabelDto;
import com.julia.taskmanagementapp.dto.label.UpdateLabelRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LabelService {
    LabelDto create(CreateLabelRequestDto requestDto);

    Page<LabelDto> getLabels(Pageable pageable);

    LabelDto update(Long id, UpdateLabelRequestDto requestDto);

    void delete(Long id);
}
