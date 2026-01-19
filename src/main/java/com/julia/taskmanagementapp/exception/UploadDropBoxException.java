package com.julia.taskmanagementapp.exception;

public class UploadDropBoxException extends RuntimeException {
    public UploadDropBoxException(String message) {
        super(message);
    }

    public UploadDropBoxException(String message, Throwable cause) {
        super(message, cause);
    }
}
