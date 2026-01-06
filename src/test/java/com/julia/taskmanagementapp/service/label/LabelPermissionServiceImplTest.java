package com.julia.taskmanagementapp.service.label;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.julia.taskmanagementapp.exception.ForbiddenAccessException;
import com.julia.taskmanagementapp.model.Label;
import com.julia.taskmanagementapp.repository.LabelRepository;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LabelPermissionServiceImplTest {
    public static final long USER_ID = 1L;
    @Mock
    private LabelRepository labelRepository;

    @InjectMocks
    private LabelPermissionServiceImpl labelPermissionService;

    @Test
    void checkLabelsIfCreator_allLabelsBelongToUser_success() {
        Set<Long> ids = Set.of(1L, 2L, 3L);

        when(labelRepository.countByIdInAndCreatorId(ids, USER_ID))
                .thenReturn(3L);

        labelPermissionService.checkLabelsIfCreator(ids, USER_ID);

        verify(labelRepository).countByIdInAndCreatorId(ids, USER_ID);
    }

    @Test
    void checkLabelsIfCreator_someLabelsNotBelong_throwException() {
        Set<Long> ids = Set.of(1L, 2L, 3L);

        when(labelRepository.countByIdInAndCreatorId(ids, USER_ID))
                .thenReturn(2L);

        assertThatThrownBy(
                () -> labelPermissionService.checkLabelsIfCreator(
                        ids, USER_ID
                ))
                .isInstanceOf(ForbiddenAccessException.class)
                .hasMessageContaining("no permission to use one or more");
    }

    @Test
    void findLabelIfCreator_existsAndBelongs_returnLabel() {
        Long labelId = 1L;

        Label label = new Label();
        label.setId(labelId);

        when(labelRepository.findByIdAndCreatorId(labelId, USER_ID))
                .thenReturn(Optional.of(label));

        Label actual = labelPermissionService.findLabelIfCreator(labelId, USER_ID);

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(labelId);
    }

    @Test
    void findLabelIfCreator_notFound_throwException() {
        Long labelId = 1L;

        when(labelRepository.findByIdAndCreatorId(labelId, USER_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> labelPermissionService.findLabelIfCreator(
                labelId, USER_ID
        ))
                .isInstanceOf(ForbiddenAccessException.class)
                .hasMessageContaining("no permission to use labels by id");
    }
}
