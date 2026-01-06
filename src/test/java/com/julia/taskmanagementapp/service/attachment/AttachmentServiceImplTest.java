package com.julia.taskmanagementapp.service.attachment;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dropbox.core.v2.files.FileMetadata;
import com.julia.taskmanagementapp.dropbox.DropBoxService;
import com.julia.taskmanagementapp.dto.attachment.AttachmentDto;
import com.julia.taskmanagementapp.exception.UploadDropBoxException;
import com.julia.taskmanagementapp.mapper.AttachmentMapper;
import com.julia.taskmanagementapp.model.Attachment;
import com.julia.taskmanagementapp.model.Task;
import com.julia.taskmanagementapp.repository.AttachmentRepository;
import com.julia.taskmanagementapp.repository.TaskRepository;
import com.julia.taskmanagementapp.service.project.ProjectPermissionService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceImplTest {
    public static final long TASK_ID = 1L;
    public static final long USER_ID = 1L;
    @Mock
    private DropBoxService dropBoxService;
    @Mock
    private AttachmentMapper attachmentMapper;
    @Mock
    private AttachmentRepository attachmentRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ProjectPermissionService projectPermissionService;
    @InjectMocks
    private AttachmentServiceImpl attachmentService;
    private MultipartFile file;
    private Task task;
    private FileMetadata metadata;
    private Attachment attachment;
    private Attachment savedAttachment;
    private AttachmentDto attachmentDto;

    @BeforeEach
    void setUp() {
        file = mock(MultipartFile.class);

        task = new Task();
        task.setId(1L);
        task.setProjectId(10L);

        metadata = mock(FileMetadata.class);

        attachment = new Attachment();

        savedAttachment = new Attachment();
        savedAttachment.setId(100L);

        attachmentDto = new AttachmentDto(
                100L, 1L, "1234",
                "file.txt", LocalDateTime.now()
        );
    }

    @Test
    void addAttachmentToTask_successNewAttachment_returnListAttachmentDtos() {
        when(file.getOriginalFilename()).thenReturn("file.txt");

        when(metadata.getId()).thenReturn("1234");

        when(taskRepository.findById(task.getId()))
                .thenReturn(Optional.of(task));
        doNothing().when(projectPermissionService).checkProjectIfCreatorOrCollaborator(
                task.getProjectId(), USER_ID
        );
        when(dropBoxService.upload(file, "/users/1/tasks/1/file.txt"))
                .thenReturn(metadata);
        when(attachmentRepository.findByTaskIdAndDropboxFileId(TASK_ID, "1234"))
                .thenReturn(Optional.empty());
        when(attachmentMapper.toModel(metadata)).thenReturn(attachment);
        when(attachmentRepository.save(attachment)).thenReturn(savedAttachment);
        when(attachmentMapper.toDto(savedAttachment)).thenReturn(attachmentDto);

        List<AttachmentDto> actual = attachmentService.addAttachmentToTask(
                task.getId(), List.of(file), USER_ID
        );

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).dropboxFileId()).isEqualTo("1234");

        verify(dropBoxService).upload(file, "/users/1/tasks/1/file.txt");
        verify(taskRepository).findById(task.getId());
        verify(attachmentRepository).save(attachment);
    }

    @Test
    void addAttachmentToTask_successExistingAttachment_returnListAttachmentDtos() {
        when(file.getOriginalFilename()).thenReturn("test.txt");
        when(metadata.getId()).thenReturn("1234");

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        doNothing().when(projectPermissionService).checkProjectIfCreatorOrCollaborator(
                task.getProjectId(), USER_ID
        );
        when(dropBoxService.upload(file, "/users/1/tasks/1/test.txt"))
                .thenReturn(metadata);
        when(attachmentRepository.findByTaskIdAndDropboxFileId(task.getId(), "1234"))
                .thenReturn(Optional.of(attachment));
        when(attachmentMapper.toDto(attachment)).thenReturn(attachmentDto);

        List<AttachmentDto> actual = attachmentService.addAttachmentToTask(
                task.getId(), List.of(file), USER_ID);

        assertThat(actual).hasSize(1);
        verify(attachmentRepository, never()).save(any());
        verify(attachmentRepository).findByTaskIdAndDropboxFileId(task.getId(), "1234");
    }

    @Test
    void addAttachmentToTask_noFiles_throwsException() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        assertThatThrownBy(
                () -> attachmentService.addAttachmentToTask(
                        task.getId(), List.of(), USER_ID))
                .isInstanceOf(UploadDropBoxException.class);
    }

    @Test
    void downloadAttachments_singleFile_downloadsFile() throws IOException {
        attachment.setDropboxFileId("1234");
        attachment.setFilename("doc.pdf");

        ServletOutputStream out = mock(ServletOutputStream.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        doNothing().when(projectPermissionService)
                .checkProjectIfCreatorOrCollaborator(task.getProjectId(), USER_ID);
        when(attachmentRepository.findAllByTaskId(task.getId()))
                .thenReturn(List.of(attachment));
        when(response.getOutputStream()).thenReturn(out);

        attachmentService.downloadAttachments(task.getId(), response, USER_ID);

        verify(dropBoxService).downloadSingleFile("1234", out);
        verify(out).flush();
        verify(taskRepository).findById(task.getId());
        verify(projectPermissionService)
                .checkProjectIfCreatorOrCollaborator(task.getProjectId(), USER_ID);
    }

    @Test
    void downloadAttachments_multipleFiles_downloadsZip() throws IOException {
        Attachment attachmentFirst = new Attachment();
        Attachment attachmentSecond = new Attachment();

        ServletOutputStream out = mock(ServletOutputStream.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(taskRepository.findById(task.getId()))
                .thenReturn(Optional.of(task));
        doNothing().when(projectPermissionService)
                .checkProjectIfCreatorOrCollaborator(task.getProjectId(), USER_ID);
        when(attachmentRepository.findAllByTaskId(task.getId()))
                .thenReturn(List.of(attachmentFirst, attachmentSecond));
        when(response.getOutputStream()).thenReturn(out);

        attachmentService.downloadAttachments(task.getId(), response, USER_ID);

        verify(dropBoxService).downloadFilesAsZip("/users/1/tasks/1", out);
        verify(taskRepository).findById(task.getId());
        verify(projectPermissionService)
                .checkProjectIfCreatorOrCollaborator(task.getProjectId(), USER_ID);
        verify(attachmentRepository).findAllByTaskId(task.getId());
    }

    @Test
    void deleteAttachment_valid_deletesAttachment() {
        String fileId = "1234";

        attachment.setDropboxFileId(fileId);
        attachment.setTaskId(task.getId());

        when(attachmentRepository.findByDropboxFileId(fileId))
                .thenReturn(Optional.of(attachment));
        when(taskRepository.findById(task.getId()))
                .thenReturn(Optional.of(task));

        attachmentService.deleteAttachment(fileId, USER_ID);

        verify(dropBoxService).deleteFile(fileId);
        verify(attachmentRepository).delete(attachment);
        verify(attachmentRepository).findByDropboxFileId(fileId);
        verify(taskRepository).findById(task.getId());
        verify(projectPermissionService)
                .checkProjectIfCreatorOrCollaborator(task.getProjectId(), USER_ID);
    }
}
