package com.bond.taskservice.api.error;

import com.bond.taskservice.api.exception.TaskNotFoundException;
import com.bond.taskservice.api.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(HandlerMethodValidationException ex,
                                                             HttpServletRequest request) {
        log.error("MethodArgumentNotValid exception path={}", request.getRequestURI(), ex);

        List<ApiFieldError> details = new ArrayList<>();

        for (var paramResult : ex.getParameterValidationResults()) { // per-param results [1](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/method/annotation/HandlerMethodValidationException.html)
            String paramName = paramResult.getMethodParameter().getParameterName(); // can be null if not compiled with -parameters

            // 1) Nested bean validation errors: these typically adapt to FieldError
            for (MessageSourceResolvable resolvable : paramResult.getResolvableErrors()) {
                if (resolvable instanceof FieldError fe) {
                    // field inside the object (e.g. "taskName")
                    details.add(new ApiFieldError(fe.getField(), fe.getDefaultMessage()));
                } else {
                    // 2) Direct param constraint (e.g. request param/path variable)
                    // Fall back to parameter name + message
                    String field = (paramName != null ? paramName : paramResult.getMethodParameter().getParameterName());
                    details.add(new ApiFieldError(field != null ? field : "parameter", resolvable.getDefaultMessage()));
                }
            }
        }

        ApiErrorResponse body = new ApiErrorResponse(
                OffsetDateTime.now(),
                request.getRequestURI(),
                "VALIDATION_ERROR",
                "Validation failed",
                details
        );

        return ResponseEntity.badRequest().body(body);

    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(TaskNotFoundException ex,
                                                          HttpServletRequest request) {

        ApiErrorResponse body = new ApiErrorResponse(
                OffsetDateTime.now(),
                request.getRequestURI(),
                "NOT_FOUND",
                ex.getMessage(),
                List.of()
        );

        return ResponseEntity.status(404).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex,
                                                         HttpServletRequest request) {
        log.error("Unhandled exception path={}", request.getRequestURI(), ex);
        ApiErrorResponse body = new ApiErrorResponse(
                OffsetDateTime.now(),
                request.getRequestURI(),
                "INTERNAL_ERROR",
                "Something went wrong",
                List.of()
        );

        return ResponseEntity.status(500).body(body);
    }
}