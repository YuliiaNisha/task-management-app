package com.julia.taskmanagementapp.controller;

import com.julia.taskmanagementapp.dto.attachment.AttachmentDto;
import com.julia.taskmanagementapp.model.User;
import com.julia.taskmanagementapp.repository.AttachmentRepository;
import com.julia.taskmanagementapp.service.attachment.AttachmentService;
import com.julia.taskmanagementapp.service.project.ProjectPermissionService;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/attachments")
public class AttachmentController {
    private final AttachmentService attachmentService;
    private final AttachmentRepository attachmentRepository;
    private final ProjectPermissionService projectPermissionService;

    @PostMapping
    public List<AttachmentDto> addAttachmentToTask(
            @RequestParam("taskId") Long taskId,
            @RequestParam("file") List<MultipartFile> files,
            @AuthenticationPrincipal User user
    ) {
        return attachmentService.addAttachmentToTask(taskId, files, user.getId());
    }

    @GetMapping
    public void downloadTaskAttachments(
            @RequestParam ("taskId") Long taskId,
            HttpServletResponse response,
            @AuthenticationPrincipal User user
    ) {
        attachmentService.downloadAttachments(taskId, response, user.getId());
    }
}
