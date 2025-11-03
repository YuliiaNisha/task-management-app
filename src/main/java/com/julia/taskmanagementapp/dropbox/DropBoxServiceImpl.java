package com.julia.taskmanagementapp.dropbox;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.NetworkIOException;
import com.dropbox.core.RetryException;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.DbxPathV2;
import com.dropbox.core.v2.files.CommitInfo;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadSessionCursor;
import com.dropbox.core.v2.files.UploadSessionFinishErrorException;
import com.dropbox.core.v2.files.WriteMode;
import com.julia.taskmanagementapp.exception.DownloadDropBoxException;
import com.julia.taskmanagementapp.exception.UploadDropBoxException;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DropBoxServiceImpl implements DropBoxService {
    private static final long CHUNKED_UPLOAD_CHUNK_SIZE = 8L << 20;
    private static final int CHUNKED_UPLOAD_MAX_ATTEMPTS = 5;
    private static final String CLIENT_IDENTIFIER = "task_management_application";
    private DbxClientV2 dbxClient;
    @Value("${dropbox.appKey:dummy-key}")
    private String appKey;
    @Value("${dropbox.appSecret:dummy-secret}")
    private String appSecret;
    @Value("${dropbox.refreshToken:dummy-token}")
    private String refreshToken;

    @PostConstruct
    public void init() {
        DbxCredential credential = new DbxCredential(
                "",
                0L,
                refreshToken,
                appKey,
                appSecret
        );

        DbxRequestConfig config = DbxRequestConfig.newBuilder(CLIENT_IDENTIFIER).build();
        this.dbxClient = new DbxClientV2(config, credential);
    }

//    public DropBoxServiceImpl() {
//        DbxCredential credential = new DbxCredential(
//                "",
//                0L,
//                refreshToken,
//                appKey,
//                appSecret
//        );
//
//        DbxRequestConfig config = DbxRequestConfig.newBuilder(CLIENT_IDENTIFIER).build();
//        this.dbxClient = new DbxClientV2(config, credential);
//    }

    @Override
    public FileMetadata upload(MultipartFile file, String dropboxPath) {
        String pathError = DbxPathV2.findError(dropboxPath);
        if (pathError != null) {
            throw new UploadDropBoxException("Invalid Dropbox path: " + pathError);
        }
        if (file.isEmpty()) {
            throw new UploadDropBoxException("Uploaded file is empty");
        }

        if (file.getSize() <= (2 * CHUNKED_UPLOAD_CHUNK_SIZE)) {
            return uploadFile(file, dropboxPath);
        } else {
            return chunkedUploadFile(file, dropboxPath);
        }
    }

    @Override
    public void downloadSingleFile(String dropboxFileId, OutputStream out) {
        try {
            dbxClient.files()
                    .downloadBuilder(dropboxFileId)
                    .download(out);
        } catch (DbxException | IOException ex) {
            throw new DownloadDropBoxException("Error downloading file from Dropbox: "
                    + ex.getMessage() + " .", ex);
        }
    }

    @Override
    public void downloadFilesAsZip(String dropboxFolderPath, OutputStream out) {
        try {
            dbxClient.files().downloadZipBuilder(dropboxFolderPath).download(out);
            out.flush();
        } catch (DbxException | IOException ex) {
            throw new DownloadDropBoxException("Error downloading files from Dropbox: "
                    + ex.getMessage(), ex);
        }
    }

    private FileMetadata uploadFile(MultipartFile file, String dropboxPath) {
        try (InputStream in = file.getInputStream()) {
            return dbxClient.files().uploadBuilder(dropboxPath)
                    .withMode(WriteMode.ADD)
                    .withAutorename(true)
                    .uploadAndFinish(in);
        } catch (DbxException ex) {
            throw new UploadDropBoxException("Error uploading to Dropbox: "
                    + ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new UploadDropBoxException("Error reading from file \""
                    + file.getOriginalFilename() + "\": " + ex.getMessage());
        }
    }

    private FileMetadata chunkedUploadFile(MultipartFile file, String dropboxPath) {
        long uploaded = 0L;
        DbxException thrown = null;
        String sessionId = null;
        for (int i = 0; i < CHUNKED_UPLOAD_MAX_ATTEMPTS; ++i) {
            try (InputStream in = file.getInputStream()) {
                in.skip(uploaded);

                if (sessionId == null) {
                    sessionId = dbxClient.files().uploadSessionStart()
                            .uploadAndFinish(in, CHUNKED_UPLOAD_CHUNK_SIZE)
                            .getSessionId();
                    uploaded += CHUNKED_UPLOAD_CHUNK_SIZE;
                }

                UploadSessionCursor cursor = new UploadSessionCursor(sessionId, uploaded);

                long size = file.getSize();
                while ((size - uploaded) > CHUNKED_UPLOAD_CHUNK_SIZE) {
                    dbxClient.files().uploadSessionAppendV2(cursor)
                            .uploadAndFinish(in, CHUNKED_UPLOAD_CHUNK_SIZE);
                    uploaded += CHUNKED_UPLOAD_CHUNK_SIZE;
                    cursor = new UploadSessionCursor(sessionId, uploaded);
                }

                long remaining = size - uploaded;
                CommitInfo commitInfo = CommitInfo.newBuilder(dropboxPath)
                        .withMode(WriteMode.ADD)
                        .withAutorename(true)
                        .build();
                return dbxClient.files().uploadSessionFinish(cursor, commitInfo)
                        .uploadAndFinish(in, remaining);
            } catch (RetryException | NetworkIOException ex) {
                thrown = ex;
            } catch (UploadSessionFinishErrorException ex) {
                if (ex.errorValue.isLookupFailed()
                        && ex.errorValue.getLookupFailedValue().isIncorrectOffset()) {
                    thrown = ex;
                    uploaded = ex.errorValue
                            .getLookupFailedValue()
                            .getIncorrectOffsetValue()
                            .getCorrectOffset();
                } else {
                    throw new UploadDropBoxException("Error uploading to Dropbox: "
                            + ex.getMessage(), ex);
                }
            } catch (DbxException ex) {
                throw new UploadDropBoxException("Error uploading to Dropbox: "
                        + ex.getMessage(), ex);
            } catch (IOException ex) {
                throw new UploadDropBoxException("Error reading from file \""
                        + file.getOriginalFilename()
                        + "\": " + ex.getMessage(), ex);
            }
        }
        throw new UploadDropBoxException("Maxed out upload attempts to Dropbox. Most recent error: "
                + thrown.getMessage());
    }
}
