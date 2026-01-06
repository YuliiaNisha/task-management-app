package com.julia.taskmanagementapp.service.label;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.julia.taskmanagementapp.dto.label.CreateLabelRequestDto;
import com.julia.taskmanagementapp.dto.label.LabelDto;
import com.julia.taskmanagementapp.dto.label.UpdateLabelRequestDto;
import com.julia.taskmanagementapp.exception.EntityAlreadyExistsException;
import com.julia.taskmanagementapp.mapper.LabelMapper;
import com.julia.taskmanagementapp.model.Label;
import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.repository.LabelRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class LabelServiceImplTest {
    @Mock
    private LabelRepository labelRepository;
    @Mock
    private LabelMapper labelMapper;
    @InjectMocks
    private LabelServiceImpl labelService;
    private Label label;
    private Label savedLabel;
    private LabelDto labelDto;
    private CreateLabelRequestDto createLabelRequestDto;
    private CreateLabelRequestDto createLabelDtoTrimmed;
    private Long userId = 1L;
    private User user;
    private Pageable pageable;
    private UpdateLabelRequestDto updateRequestDto;
    private UpdateLabelRequestDto trimmedUpdateRequestDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        label = new Label();

        savedLabel = new Label();

        labelDto = new LabelDto(1L, "important", "red", 1L);

        createLabelRequestDto = new CreateLabelRequestDto(
                "important", "red"
        );
        createLabelDtoTrimmed = new CreateLabelRequestDto(
                "important", "red"
        );
        pageable = PageRequest.of(0, 10);
        updateRequestDto = new UpdateLabelRequestDto(
                "new name", "new color"
        );
        trimmedUpdateRequestDto = new UpdateLabelRequestDto(
                "new name", "new color"
        );
    }

    @Test
    void create_validRequest_returnsLabelDto() {
        when(labelMapper.trimCreateRequest(createLabelRequestDto))
                .thenReturn(createLabelDtoTrimmed);
        when(labelRepository.existsByNameIgnoreCaseAndCreatorId(
                createLabelDtoTrimmed.name(), userId
        )).thenReturn(false);
        when(labelRepository.existsByColorIgnoreCaseAndCreatorId(
                createLabelDtoTrimmed.color(), userId
        )).thenReturn(false);
        when(labelMapper.toModel(createLabelDtoTrimmed))
                .thenReturn(label);
        when(labelRepository.save(label))
                .thenReturn(savedLabel);
        when(labelMapper.toDto(savedLabel))
                .thenReturn(labelDto);

        LabelDto actual = labelService.create(createLabelRequestDto, user);

        assertEquals(labelDto, actual);

        verify(labelRepository).existsByNameIgnoreCaseAndCreatorId(
                createLabelDtoTrimmed.name(), userId
        );
        verify(labelRepository).existsByColorIgnoreCaseAndCreatorId(
                createLabelDtoTrimmed.color(), userId
        );
    }

    @Test
    void create_labelNameAlreadyExists_throwsException() {
        when(labelMapper.trimCreateRequest(createLabelRequestDto))
                .thenReturn(createLabelDtoTrimmed);
        when(labelRepository.existsByNameIgnoreCaseAndCreatorId(
                createLabelDtoTrimmed.name(), userId
        )).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class,
                () -> labelService.create(
                        createLabelRequestDto, user
                ));

        verify(labelRepository).existsByNameIgnoreCaseAndCreatorId(
                createLabelDtoTrimmed.name(), userId
        );
    }

    @Test
    void getLabels_validRequest_returnsPageDtos() {
        PageImpl<Label> labels = new PageImpl<>(List.of(label));
        PageImpl<LabelDto> labelDtos = new PageImpl<>(List.of(labelDto));

        when(labelRepository.findAllByCreatorId(userId, pageable))
                .thenReturn(labels);
        when(labelMapper.toDto(label))
                .thenReturn(labelDto);

        Page<LabelDto> actual = labelService.getLabels(userId, pageable);

        assertEquals(labelDtos, actual);
    }

    @Test
    void update() {
        when(labelRepository.findByIdAndCreatorId(1L, userId))
                .thenReturn(Optional.ofNullable(label));
        when(labelMapper.trimUpdateRequest(updateRequestDto))
                .thenReturn(trimmedUpdateRequestDto);
        when(labelRepository.existsByNameIgnoreCaseAndCreatorId(
                trimmedUpdateRequestDto.name(), userId
        )).thenReturn(false);
        when(labelRepository.existsByColorIgnoreCaseAndCreatorId(
                trimmedUpdateRequestDto.color(), userId
        )).thenReturn(false);
        doNothing().when(labelMapper).update(label, trimmedUpdateRequestDto);
        when(labelRepository.save(label)).thenReturn(savedLabel);
        when(labelMapper.toDto(savedLabel)).thenReturn(labelDto);

        LabelDto actual = labelService.update(1L, updateRequestDto, userId);

        assertEquals(labelDto, actual);

        verify(labelRepository).findByIdAndCreatorId(1L, userId);
        verify(labelRepository).existsByNameIgnoreCaseAndCreatorId(
                trimmedUpdateRequestDto.name(), userId);
        verify(labelRepository).existsByColorIgnoreCaseAndCreatorId(
                trimmedUpdateRequestDto.color(), userId);
    }

    @Test
    void delete() {
        when(labelRepository.findByIdAndCreatorId(1L, userId))
                .thenReturn(Optional.ofNullable(label));
        doNothing().when(labelRepository).delete(label);

        assertDoesNotThrow(() -> labelService.delete(
                1L, userId
        ));

        verify(labelRepository).findByIdAndCreatorId(1L, userId);
    }
}
