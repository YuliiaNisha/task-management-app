package com.julia.taskmanagementapp.service.attachment;

import com.dropbox.core.v2.files.FileMetadata;
import com.julia.taskmanagementapp.dropbox.DropBoxService;
import com.julia.taskmanagementapp.dto.attachment.AttachmentDto;
import com.julia.taskmanagementapp.exception.DownloadDropBoxException;
import com.julia.taskmanagementapp.exception.EntityNotFoundException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class AttachmentServiceImpl implements AttachmentService {
    private final DropBoxService dropBoxService;
    private final AttachmentMapper attachmentMapper;
    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final ProjectPermissionService projectPermissionService;

    @Override
    public List<AttachmentDto> addAttachmentToTask(
            Long taskId, List<MultipartFile> files, Long userId
    ) {
        checkUserHasPermissionToModifyTask(taskId, userId);

        if (files == null || files.isEmpty()) {
            throw new UploadDropBoxException("No files provided for upload.");
        }

        List<AttachmentDto> attachmentDtos = new ArrayList<>();
        for (MultipartFile file : files) {
            String dropboxPath = "/users/" + userId
                    + "/tasks/" + taskId
                    + "/" + file.getOriginalFilename();
            FileMetadata metadata = dropBoxService.upload(file, dropboxPath);

            Optional<Attachment> attachmentByTaskAndId =
                    attachmentRepository.findByTaskIdAndDropboxFileId(
                    taskId, metadata.getId()
            );
            if (attachmentByTaskAndId.isPresent()) {
                attachmentDtos.add(attachmentMapper.toDto(attachmentByTaskAndId.get()));
                continue;
            }

            Attachment attachment = attachmentMapper.toModel(metadata);
            attachment.setTaskId(taskId);
            Attachment savedAttachment = attachmentRepository.save(attachment);
            attachmentDtos.add(attachmentMapper.toDto(savedAttachment));
        }
        return attachmentDtos;
    }

    @Override
    public void downloadAttachments(Long taskId, HttpServletResponse response, Long userId) {
        checkUserHasPermissionToModifyTask(taskId, userId);

        List<Attachment> attachments = attachmentRepository.findAllByTaskId(taskId);
        if (attachments.isEmpty()) {
            throw new EntityNotFoundException("There are no attachments to task by id: " + taskId);
        }

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            if (attachments.size() == 1) {
                Attachment attachment = attachments.get(0);
                response.setHeader("Content-Disposition", "attachment; filename=\""
                        + attachment.getFilename() + "\"");
                response.setContentType("application/octet-stream");
                dropBoxService.downloadSingleFile(attachment.getDropboxFileId(), outputStream);
            } else {
                response.setHeader("Content-Disposition",
                        "attachment; filename=\"attachments.zip\"");
                response.setContentType("application/zip");
                String dropboxFolderPath = "/users/" + userId + "/tasks/" + taskId;
                dropBoxService.downloadFilesAsZip(dropboxFolderPath, outputStream);
            }
            outputStream.flush();
        } catch (IOException e) {
            throw new DownloadDropBoxException("Failed to download attachments from Dropbox", e);
        }
    }

    private void checkUserHasPermissionToModifyTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new EntityNotFoundException(
                        "There is no task by id: " + taskId
                )
        );
        projectPermissionService.checkProjectIfCreatorOrCollaborator(
                task.getProjectId(), userId
        );
    }
}
