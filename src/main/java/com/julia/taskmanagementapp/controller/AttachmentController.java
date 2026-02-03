package com.julia.taskmanagementapp.controller;

import com.julia.taskmanagementapp.dto.attachment.AttachmentDto;
import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.repository.AttachmentRepository;
import com.julia.taskmanagementapp.service.attachment.AttachmentService;
import com.julia.taskmanagementapp.service.project.ProjectPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Attachments",
        description = "Endpoints for managing attachments")
@RequiredArgsConstructor
@RestController
@RequestMapping("/attachments")
public class AttachmentController {
    private final AttachmentService attachmentService;
    private final AttachmentRepository attachmentRepository;
    private final ProjectPermissionService projectPermissionService;

    @Operation(
            summary = "Add attachments to a task",
            description = "Adds one or more attachments to the specified task. "
                    + "Only the creator of the project or collaborators "
            + "of the project associated with the task are permitted to attach files.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Attachments added successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request data or file(s)"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorised – user is not authenticated"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden – only the project’s creator "
                            + "or collaborators may attach files to this task"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Task not found"
                    )
            }
    )
    @PostMapping
    public List<AttachmentDto> addAttachmentToTask(
            @RequestParam("taskId") @Positive(message = "ID must be positive") Long taskId,
            @RequestParam("file") List<MultipartFile> files,
            @AuthenticationPrincipal User user
    ) {
        return attachmentService.addAttachmentToTask(taskId, files, user.getId());
    }

    @Operation(
            summary = "Download attachments for a task",
            description = "Downloads all attachments for the specified task. "
                    + "Only the creator of the project or collaborators "
            + "of the project associated with the task may download attachments.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Attachments downloaded successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid task ID"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorised – user is not authenticated"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden – only the project’s creator "
                            + "or collaborators may download attachments for this task"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Task not found"
                    )
            }
    )
    @GetMapping
    public void downloadTaskAttachments(
            @RequestParam ("taskId") @Positive(message = "ID must be positive") Long taskId,
            HttpServletResponse response,
            @AuthenticationPrincipal User user
    ) {
        attachmentService.downloadAttachments(taskId, response, user.getId());
    }

    @Operation(
            summary = "Delete an attachment",
            description = "Deletes an attachment specified by its Dropbox file ID. "
                    + "Only the creator of the project or collaborators "
            + "of the project associated with the task may delete attachments.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Attachment deleted successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid Dropbox file ID"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorised – user is not authenticated"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden – only the project’s creator "
                            + "or collaborators may delete this attachment"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Attachment not found"
                    )
            }
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public void deleteAttachment(
            @RequestParam ("dropboxFileId") @NotBlank String dropboxFileId,
            @AuthenticationPrincipal User user
    ) {
        attachmentService.deleteAttachment(dropboxFileId, user.getId());
    }
}
