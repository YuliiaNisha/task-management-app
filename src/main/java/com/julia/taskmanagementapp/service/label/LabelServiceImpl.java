package com.julia.taskmanagementapp.service.label;

import com.julia.taskmanagementapp.dto.label.CreateLabelRequestDto;
import com.julia.taskmanagementapp.dto.label.LabelDto;
import com.julia.taskmanagementapp.exception.EntityAlreadyExistsException;
import com.julia.taskmanagementapp.mapper.LabelMapper;
import com.julia.taskmanagementapp.model.Label;
import com.julia.taskmanagementapp.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LabelServiceImpl implements LabelService {
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    @Override
    public LabelDto create(CreateLabelRequestDto requestDto) {
        labelNameExists(requestDto.name().trim());
        labelColorExists(requestDto.color().trim());
        Label label = labelMapper.toModel(requestDto);
        return labelMapper.toDto(labelRepository.save(label));
    }

    private void labelColorExists(String color) {
        if (labelRepository.existsByColorIgnoreCase(color)) {
            throw new EntityAlreadyExistsException(
                    "Cannot create label with color '" + color
                            + "' - This color is already in use. Please choose another color."
            );
        }
    }

    private void labelNameExists(String name) {
        if (labelRepository.existsByNameIgnoreCase(name)) {
            throw new EntityAlreadyExistsException(
                    "Label with name '" + name
                            + "' already exists. Please choose a different name."
            );
        }
    }
}
