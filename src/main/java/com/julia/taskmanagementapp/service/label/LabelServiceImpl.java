package com.julia.taskmanagementapp.service.label;

import com.julia.taskmanagementapp.dto.label.CreateLabelRequestDto;
import com.julia.taskmanagementapp.dto.label.LabelDto;
import com.julia.taskmanagementapp.dto.label.UpdateLabelRequestDto;
import com.julia.taskmanagementapp.exception.EntityAlreadyExistsException;
import com.julia.taskmanagementapp.exception.EntityNotFoundException;
import com.julia.taskmanagementapp.mapper.LabelMapper;
import com.julia.taskmanagementapp.model.Label;
import com.julia.taskmanagementapp.repository.LabelRepository;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LabelServiceImpl implements LabelService {
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    @Override
    public LabelDto create(CreateLabelRequestDto requestDto) {
        CreateLabelRequestDto trimmedRequestDto = labelMapper.trimCreateRequest(requestDto);
        checkValuesAlreadyExist(trimmedRequestDto.name(), trimmedRequestDto.color());
        Label label = labelMapper.toModel(trimmedRequestDto);
        return labelMapper.toDto(labelRepository.save(label));
    }

    @Override
    public Page<LabelDto> getLabels(Pageable pageable) {
        return labelRepository.findAll(pageable)
                .map(labelMapper::toDto);
    }

    @Override
    public LabelDto update(Long id, UpdateLabelRequestDto requestDto) {
        Label label = findLabelById(id);
        UpdateLabelRequestDto trimmedRequestDto = labelMapper.trimUpdateRequest(requestDto);
        checkValuesAlreadyExistOrDuplicate(
                trimmedRequestDto.name(),
                trimmedRequestDto.color(),
                label.getName(),
                label.getColor()
        );
        labelMapper.update(label, trimmedRequestDto);
        return labelMapper.toDto(labelRepository.save(label));
    }

    @Override
    public void delete(Long id) {
        Label label = findLabelById(id);
        labelRepository.delete(label);
    }

    private void checkValuesAlreadyExistOrDuplicate(
            String name, String color,
            String currentName, String currentColor
    ) {
        if (name != null && !name.equalsIgnoreCase(currentName)) {
            checkField(
                    name,
                    labelRepository::existsByNameIgnoreCase,
                    new EntityAlreadyExistsException(
                            "Cannot update label with name '" + name
                                    + "' because it already exists. Please choose a different name."
                    )
            );
        }
        if (color != null && !color.equalsIgnoreCase(currentColor)) {
            checkField(
                    color,
                    labelRepository::existsByColorIgnoreCase,
                    new EntityAlreadyExistsException(
                            "Cannot update label with color '" + color
                                    + "' - This color is already in use. Please choose another color."
                    )
            );
        }
    }

    private void checkValuesAlreadyExist(String name, String color) {
        if (name != null) {
            checkField(
                    name,
                    labelRepository::existsByNameIgnoreCase,
                    new EntityAlreadyExistsException("Label with name '" + name
                            + "' already exists. Please choose a different name.")
            );
        }
        if (color != null) {
            checkField(
                    color,
                    labelRepository::existsByColorIgnoreCase,
                    new EntityAlreadyExistsException("Cannot create label with color '" + color
                            + "' - This color is already in use. Please choose another color.")
            );
        }
    }

    private void checkField(
            String fieldValue,
            Predicate<String> predicate,
            RuntimeException exception) {
        if (predicate.test(fieldValue)) {
            throw exception;
        }
    }

    private Label findLabelById(Long id) {
        return labelRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        "There is no label by id: " + id
                )
        );
    }
}
