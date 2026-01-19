package com.julia.taskmanagementapp.mapper;

import com.dropbox.core.v2.files.FileMetadata;
import com.julia.taskmanagementapp.config.MapperConfig;
import com.julia.taskmanagementapp.dto.attachment.AttachmentDto;
import com.julia.taskmanagementapp.model.Attachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface AttachmentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dropboxFileId", source = "id")
    @Mapping(target = "filename", source = "name")
    @Mapping(target = "uploadDate", expression = "java(java.time.LocalDateTime.now())")
    Attachment toModel(FileMetadata metadata);

    AttachmentDto toDto(Attachment attachment);
}
