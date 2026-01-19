package com.julia.taskmanagementapp.repository;

import com.julia.taskmanagementapp.model.Attachment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findAllByTaskId(Long taskId);

    Optional<Attachment> findByTaskIdAndDropboxFileId(Long taskId, String dropboxFileId);

    Optional<Attachment> findByDropboxFileId(String dropboxFileId);
}
