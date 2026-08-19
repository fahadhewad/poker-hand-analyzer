package com.pokeranalyzer.web;

import com.pokeranalyzer.web.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(InvalidHandHistoryException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInput(InvalidHandHistoryException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("invalid_input", e.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("malformed_request", "Request body could not be read."));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleTooLarge(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(new ErrorResponse("payload_too_large", "Uploaded file exceeds the maximum allowed size."));
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ErrorResponse> handleMultipart(MultipartException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("invalid_upload", "Multipart upload could not be processed."));
    }
}
