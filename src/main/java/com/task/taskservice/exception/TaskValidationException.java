package com.task.taskservice.exception;

import org.springframework.web.bind.MethodArgumentNotValidException;

public class TaskValidationException extends RuntimeException {
    public TaskValidationException(String message) {
        super(message);
    }
}
