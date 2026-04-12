package com.bond.taskservice.api.error;

import java.time.OffsetDateTime;
import java.util.List;

public class ApiErrorResponse {

    private final OffsetDateTime timestamp;
    private final String path;
    private final String errorCode;
    private final String message;
    private final List<ApiFieldError> details;

    public ApiErrorResponse(OffsetDateTime timestamp,
                            String path,
                            String errorCode,
                            String message,
                            List<ApiFieldError> details) {
        this.timestamp = timestamp;
        this.path = path;
        this.errorCode = errorCode;
        this.message = message;
        this.details = details;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public List<ApiFieldError> getDetails() {
        return details;
    }
}