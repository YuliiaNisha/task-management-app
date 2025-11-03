package com.julia.taskmanagementapp.dropbox;

import com.dropbox.core.v2.files.FileMetadata;
import java.io.OutputStream;
import org.springframework.web.multipart.MultipartFile;

public interface DropBoxService {
    FileMetadata upload(MultipartFile file, String dropboxPath);

    void downloadSingleFile(String dropboxFileId, OutputStream out);

    void downloadFilesAsZip(String dropboxFolderPath, OutputStream out);

    void deleteFile(String dropboxFileId);
}
