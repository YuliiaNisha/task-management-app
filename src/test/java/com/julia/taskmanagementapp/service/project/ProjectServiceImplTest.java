package com.julia.taskmanagementapp.service.project;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.julia.taskmanagementapp.dto.project.CreateProjectRequestDto;
import com.julia.taskmanagementapp.dto.project.ProjectDto;
import com.julia.taskmanagementapp.dto.project.ProjectSearchParameters;
import com.julia.taskmanagementapp.dto.project.ProjectSearchParametersWithUserId;
import com.julia.taskmanagementapp.dto.project.UpdateProjectRequestDto;
import com.julia.taskmanagementapp.event.project.ProjectCreatedEvent;
import com.julia.taskmanagementapp.event.project.ProjectDeletedEvent;
import com.julia.taskmanagementapp.event.project.ProjectUpdatedEvent;
import com.julia.taskmanagementapp.event.project.factory.ProjectEventFactory;
import com.julia.taskmanagementapp.event.project.factory.ProjectEventType;
import com.julia.taskmanagementapp.exception.EntityAlreadyExistsException;
import com.julia.taskmanagementapp.mapper.ProjectMapper;
import com.julia.taskmanagementapp.model.Project;
import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.repository.ProjectRepository;
import com.julia.taskmanagementapp.repository.SpecificationBuilder;
import com.julia.taskmanagementapp.repository.UserRepository;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectMapper projectMapper;
    @Mock
    private ProjectPermissionService projectPermissionService;
    @Mock
    private ApplicationEventPublisher publisher;
    @Mock
    private ProjectEventFactory projectEventFactory;
    @Mock
    private SpecificationBuilder<
            Project, ProjectSearchParametersWithUserId
            > specificationBuilder;
    @InjectMocks
    private ProjectServiceImpl projectService;
    private CreateProjectRequestDto createRequestDto;
    private Project project;
    private Project savedProject;
    private ProjectDto projectDto;
    private User collaborator;
    private User creator;
    private Long userId = 1L;
    private Pageable pageable = PageRequest.of(0, 10);
    private UpdateProjectRequestDto updateRequestDto;
    private ProjectSearchParameters searchParameters;
    private ProjectSearchParametersWithUserId paramsWithUserId;

    @BeforeEach
    void setUp() {
        createRequestDto = new CreateProjectRequestDto(
                "Project", "description",
                LocalDate.now(),
                LocalDate.of(2026, 02, 20),
                Set.of(1L)
        );

        collaborator = new User();
        collaborator.setEmail("email");
        collaborator.setFirstName("First");
        collaborator.setId(1L);

        creator = new User();
        creator.setFirstName("First");
        creator.setPassword("Last");
        creator.setId(2L);

        project = new Project();
        project.setName("Project");
        project.setDescription("description");
        project.setStartDate(LocalDate.now());
        project.setEndDate(LocalDate.of(2026, 02, 20));
        project.setCollaborators(new HashSet<>());
        project.getCollaborators().add(collaborator);
        project.setCreator(creator);
        project.setId(1L);

        projectDto = new ProjectDto(
                1L, "Project", "description",
                LocalDate.now(),
                LocalDate.of(2026, 02, 20),
                Project.Status.INITIATED.name(),Set.of(1L)
        );

        updateRequestDto = new UpdateProjectRequestDto(
                "project", "description",
                LocalDate.now(),
                LocalDate.of(2026, 02,20),
                Project.Status.IN_PROGRESS.name(),
                Set.of(2L)
        );

        searchParameters = new ProjectSearchParameters(
                "name", null, null
        );
        paramsWithUserId = new ProjectSearchParametersWithUserId();
        paramsWithUserId.setUserId(1L);
    }

    @Test
    void create_validRequest_returnsProjectDto() {
        ProjectCreatedEvent projectCreatedEvent = new ProjectCreatedEvent(
                Map.of("email", "First"),
                project.getName(),
                String.join(
                        " ",
                        project.getCreator().getFirstName(),
                        project.getCreator().getLastName()
                ),
                project.getEndDate().toString(),
                project.getId().toString()
        );
        when(projectMapper.toModel(createRequestDto))
                .thenReturn(project);
        when(userRepository.findAllById(createRequestDto.collaboratorIds()))
                .thenReturn(List.of(collaborator));
        when(projectRepository.save(project))
                .thenReturn(savedProject);
        when(projectEventFactory.create(ProjectEventType.PROJECT_CREATED, savedProject))
                .thenReturn(projectCreatedEvent);
        when(projectMapper.toDto(savedProject))
                .thenReturn(projectDto);

        ProjectDto actual = projectService.create(createRequestDto, creator);

        assertEquals(projectDto, actual);

        verify(publisher).publishEvent(projectCreatedEvent);
    }

    @Test
    void create_validCreatorCollabEqual_throwsException() {
        ProjectCreatedEvent projectCreatedEvent = new ProjectCreatedEvent(
                Map.of("email", "First"),
                project.getName(),
                String.join(
                        " ",
                        project.getCreator().getFirstName(),
                        project.getCreator().getLastName()
                ),
                project.getEndDate().toString(),
                project.getId().toString()
        );
        when(projectMapper.toModel(createRequestDto))
                .thenReturn(project);

        assertThrows(EntityAlreadyExistsException.class,
                () -> projectService.create(createRequestDto, collaborator));
    }

    @Test
    void getUserProjects_validRequest_returnsPageProjectDto() {
        PageImpl<Project> projects = new PageImpl<>(List.of(project));
        PageImpl<ProjectDto> projectDtos = new PageImpl<>(List.of(projectDto));

        when(projectPermissionService.getProjectsIfCreatorOrCollaborator(
                userId, pageable
        )).thenReturn(projects);
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        Page<ProjectDto> actual = projectService.getUserProjects(userId, pageable);

        assertEquals(projectDtos, actual);
    }

    @Test
    void getProjectById() {
        when(projectPermissionService.getProjectByIdIfCreatorOrCollaborator(
                1L, userId
        )).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        ProjectDto actual = projectService.getProjectById(1L, userId);
        assertEquals(projectDto, actual);
    }

    @Test
    void update_validRequest_returnsProjectDto() {
        ProjectUpdatedEvent projectUpdatedEvent = new ProjectUpdatedEvent(
                Map.of("email", "First"),
                project.getName(),
                String.join(
                        " ",
                        project.getCreator().getFirstName(),
                        project.getCreator().getLastName()
                ),
                project.getEndDate().toString(),
                project.getId().toString()
        );

        when(projectPermissionService.getProjectByIdIfCreator(1L, userId))
                .thenReturn(project);
        doNothing().when(projectMapper).update(project, updateRequestDto);
        when(userRepository.findAllById(updateRequestDto.collaboratorIds()))
                .thenReturn(List.of(collaborator));
        when(projectRepository.save(project))
                .thenReturn(savedProject);
        when(projectEventFactory.create(ProjectEventType.PROJECT_UPDATED, savedProject))
                .thenReturn(projectUpdatedEvent);
        when(projectMapper.toDto(savedProject))
                .thenReturn(projectDto);

        ProjectDto actual = projectService.update(1L, updateRequestDto, userId);

        assertEquals(projectDto, actual);
    }

    @Test
    void delete_validRequest_void() {
        ProjectDeletedEvent projectDeletedEvent = new ProjectDeletedEvent(
                Map.of("email", "First"),
                project.getName(),
                String.join(
                        " ",
                        project.getCreator().getFirstName(),
                        project.getCreator().getLastName()
                ),
                project.getEndDate().toString(),
                project.getId().toString()
        );
        when(projectPermissionService.getProjectByIdIfCreator(1L, userId))
                .thenReturn(project);
        doNothing().when(projectRepository).delete(project);
        when(projectEventFactory.create(ProjectEventType.PROJECT_DELETED, project))
                .thenReturn(projectDeletedEvent);

        assertDoesNotThrow(
                () -> projectService.delete(1L, userId)
        );
    }

    @Test
    void search_validRequest_retornsPageProjectDtos() {
        PageImpl<Project> projects = new PageImpl<>(List.of(project));

        when(projectMapper.toParamsWithUserId(searchParameters))
                .thenReturn(paramsWithUserId);
        when(specificationBuilder.build(paramsWithUserId))
                .thenReturn(Specification.unrestricted());
        when(projectRepository.findAll(Specification.unrestricted(), pageable))
                .thenReturn(projects);
        when(projectMapper.toDto(project))
                .thenReturn(projectDto);

        Page<ProjectDto> actual = projectService.search(searchParameters, pageable, userId);
        PageImpl<ProjectDto> projectDtos = new PageImpl<>(List.of(projectDto));
        assertEquals(projectDtos, actual);
    }
}
