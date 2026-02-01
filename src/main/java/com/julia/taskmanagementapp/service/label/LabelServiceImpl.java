package com.julia.taskmanagementapp.service.label;

import com.julia.taskmanagementapp.dto.label.CreateLabelRequestDto;
import com.julia.taskmanagementapp.dto.label.LabelDto;
import com.julia.taskmanagementapp.dto.label.UpdateLabelRequestDto;
import com.julia.taskmanagementapp.exception.EntityAlreadyExistsException;
import com.julia.taskmanagementapp.exception.ForbiddenAccessException;
import com.julia.taskmanagementapp.mapper.LabelMapper;
import com.julia.taskmanagementapp.model.Label;
import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LabelServiceImpl implements LabelService {
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    @Transactional
    @Override
    public LabelDto create(CreateLabelRequestDto requestDto, User user) {
        CreateLabelRequestDto trimmedRequestDto = labelMapper.trimCreateRequest(requestDto);
        checkValuesAlreadyExist(trimmedRequestDto.name(), trimmedRequestDto.color(), user.getId());
        Label label = labelMapper.toModel(trimmedRequestDto);
        label.setCreator(user);
        return labelMapper.toDto(labelRepository.save(label));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<LabelDto> getLabels(Long userId, Pageable pageable) {
        return labelRepository.findAllByCreatorId(userId, pageable)
                .map(labelMapper::toDto);
    }

    @Transactional
    @Override
    public LabelDto update(Long id, UpdateLabelRequestDto requestDto, Long userId) {
        Label label = getLabelForUser(id, userId);
        UpdateLabelRequestDto trimmedRequestDto = labelMapper.trimUpdateRequest(requestDto);
        checkValuesAlreadyExist(
                trimmedRequestDto.name(),
                trimmedRequestDto.color(),
                userId
        );
        labelMapper.update(label, trimmedRequestDto);
        return labelMapper.toDto(labelRepository.save(label));
    }

    @Transactional
    @Override
    public void delete(Long id, Long userId) {
        Label label = getLabelForUser(id, userId);
        labelRepository.delete(label);
    }

    private Label getLabelForUser(Long id, Long userId) {
        return labelRepository.findByIdAndCreatorId(id, userId).orElseThrow(
                () -> new ForbiddenAccessException(
                        "You do not have permission to access label with id: " + id
                )
        );
    }

    private void checkValuesAlreadyExist(String name, String color, Long userId) {
        if (name != null) {
            if (labelRepository.existsByNameIgnoreCaseAndCreatorId(name, userId)) {
                throw new EntityAlreadyExistsException("Label with name '" + name
                        + "' already exists. Please choose a different name.");
            }
        }
        if (color != null) {
            if (labelRepository.existsByColorIgnoreCaseAndCreatorId(color,userId)) {
                throw new EntityAlreadyExistsException("Cannot create label with color '" + color
                        + "' - This color is already in use. Please choose another color.");
            }
        }
    }
}
