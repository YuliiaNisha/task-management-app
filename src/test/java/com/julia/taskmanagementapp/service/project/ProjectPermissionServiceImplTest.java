package com.julia.taskmanagementapp.service.project;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.julia.taskmanagementapp.exception.ForbiddenAccessException;
import com.julia.taskmanagementapp.model.Project;
import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.repository.ProjectRepository;
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
class ProjectPermissionServiceImplTest {
    @Mock
    private ProjectRepository projectRepository;
    @InjectMocks
    private ProjectPermissionServiceImpl projectPermissionService;
    private Project project;
    private Pageable pageable;
    private User user;
    private Page<Project> projectPage;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        project = new Project();
        project.setCreator(user);
        project.setId(10L);

        pageable = PageRequest.of(0, 10);

        projectPage = new PageImpl<>(List.of(project));
    }

    @Test
    void getProjectsIfCreatorOrCollaborator_valid_returnsPageOfProjects() {
        when(projectRepository.findAllByCreatorOrCollaborators(
                user.getId(), pageable
        ))
                .thenReturn(projectPage);

        Page<Project> actual = projectPermissionService.getProjectsIfCreatorOrCollaborator(
                user.getId(), pageable
        );

        assertEquals(1, actual.getTotalElements());
        assertEquals(project, actual.getContent().get(0));
        verify(projectRepository).findAllByCreatorOrCollaborators(user.getId(), pageable);
        verifyNoMoreInteractions(projectRepository);
    }

    @Test
    void getProjectByIdIfCreatorOrCollaborator_valid_returnProjectDto() {
        when(projectRepository.findByIdAndCreatorAndCollaborators(
                project.getId(), user.getId()
        ))
                .thenReturn(Optional.of(project));

        Project actual = projectPermissionService.getProjectByIdIfCreatorOrCollaborator(
                project.getId(), user.getId()
        );

        assertNotNull(actual);
        assertEquals(project, actual);
        verify(projectRepository).findByIdAndCreatorAndCollaborators(
                project.getId(), user.getId()
        );
        verifyNoMoreInteractions(projectRepository);
    }

    @Test
    void getProjectByIdIfCreatorOrCollaborator_invalid_throwException() {
        when(projectRepository.findByIdAndCreatorAndCollaborators(
                project.getId(), user.getId()
        ))
                .thenReturn(Optional.empty());

        assertThrows(ForbiddenAccessException.class,
                () -> projectPermissionService.getProjectByIdIfCreatorOrCollaborator(
                        project.getId(), user.getId()
                ));
        verify(projectRepository).findByIdAndCreatorAndCollaborators(
                project.getId(), user.getId()
        );
        verifyNoMoreInteractions(projectRepository);
    }

    @Test
    void checkProjectIfCreatorOrCollaborator_valid_doesNotThrowException() {
        when(projectRepository.existsByIdAndCreatorAndCollaborators(
                project.getId(), user.getId()
        ))
                .thenReturn(true);

        assertDoesNotThrow(
                () -> projectPermissionService.checkProjectIfCreatorOrCollaborator(
                        project.getId(), user.getId())
        );

        verify(projectRepository).existsByIdAndCreatorAndCollaborators(
                project.getId(), user.getId()
        );
        verifyNoMoreInteractions(projectRepository);
    }

    @Test
    void checkProjectIfCreatorOrCollaborator_invalid_throwException() {
        when(projectRepository.existsByIdAndCreatorAndCollaborators(
                project.getId(), user.getId()
        ))
                .thenReturn(false);

        assertThrows(
                ForbiddenAccessException.class,
                () -> projectPermissionService.checkProjectIfCreatorOrCollaborator(
                        project.getId(), user.getId()
                )
        );

        verify(projectRepository).existsByIdAndCreatorAndCollaborators(
                project.getId(), user.getId()
        );
        verifyNoMoreInteractions(projectRepository);
    }

    @Test
    void checkProjectIfCollaborator_valid_doesNotThrowException() {
        when(projectRepository.existsByIdAndCollaborator(
                project.getId(), user.getId()
        ))
                .thenReturn(true);

        assertDoesNotThrow(
                () -> projectPermissionService.checkProjectIfCollaborator(
                        project.getId(), user.getId()
                )
        );

        verify(projectRepository).existsByIdAndCollaborator(
                project.getId(), user.getId()
        );
        verifyNoMoreInteractions(projectRepository);
    }

    @Test
    void checkProjectIfCollaborator_invalid_throwException() {
        when(projectRepository.existsByIdAndCollaborator(
                project.getId(), user.getId()
        ))
                .thenReturn(false);

        assertThrows(
                ForbiddenAccessException.class,
                () -> projectPermissionService.checkProjectIfCollaborator(
                        project.getId(), user.getId()
                )
        );

        verify(projectRepository).existsByIdAndCollaborator(
                project.getId(), user.getId()
        );
        verifyNoMoreInteractions(projectRepository);
    }

    @Test
    void checkProjectIfCreator_valid_doesNotThrowException() {
        when(projectRepository.existsByIdAndCreator(
                project.getId(), user.getId()
        ))
                .thenReturn(true);

        assertDoesNotThrow(
                () -> projectPermissionService.checkProjectIfCreator(
                        project.getId(), user.getId()
                )
        );

        verify(projectRepository).existsByIdAndCreator(
                project.getId(), user.getId()
        );
        verifyNoMoreInteractions(projectRepository);
    }

    @Test
    void checkProjectIfCreator_invalid_throwException() {
        when(projectRepository.existsByIdAndCreator(
                project.getId(), user.getId()
        ))
                .thenReturn(false);

        assertThrows(
                ForbiddenAccessException.class,
                () -> projectPermissionService.checkProjectIfCreator(
                        project.getId(), user.getId()
                )
        );

        verify(projectRepository).existsByIdAndCreator(
                project.getId(), user.getId()
        );
        verifyNoMoreInteractions(projectRepository);
    }

    @Test
    void getProjectByIdIfCreator_valid_returnProject() {
        when(projectRepository.findByIdAndCreator(
                project.getId(), user.getId()
        ))
                .thenReturn(Optional.of(project));

        Project actual = projectPermissionService.getProjectByIdIfCreator(
                project.getId(), user.getId()
        );

        assertNotNull(actual);
        verify(projectRepository).findByIdAndCreator(
                project.getId(), user.getId()
        );
        verifyNoMoreInteractions(projectRepository);
    }

    @Test
    void getProjectByIdIfCreator_invalid_throwException() {
        when(projectRepository.findByIdAndCreator(
                project.getId(), user.getId()
        ))
                .thenReturn(Optional.empty());

        assertThrows(
                ForbiddenAccessException.class,
                () -> projectPermissionService.getProjectByIdIfCreator(
                        project.getId(), user.getId()
                )
        );

        verify(projectRepository).findByIdAndCreator(
                project.getId(), user.getId()
        );
        verifyNoMoreInteractions(projectRepository);
    }
}
