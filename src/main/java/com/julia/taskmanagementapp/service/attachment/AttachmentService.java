package com.julia.taskmanagementapp.service.attachment;

import com.julia.taskmanagementapp.dto.attachment.AttachmentDto;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    List<AttachmentDto> addAttachmentToTask(Long taskId, List<MultipartFile> files, Long userId);

    void downloadAttachments(Long taskId, HttpServletResponse response, Long userId);

    void deleteAttachment(String dropboxFileId, Long userId);
}
