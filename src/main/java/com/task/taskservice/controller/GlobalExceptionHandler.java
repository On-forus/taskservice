package com.task.taskservice.controller;

import com.task.taskservice.dto.ErrorResponse;
import com.task.taskservice.exception.TaskNotFoundException;
import com.task.taskservice.exception.TaskValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String EUROPE_MOSCOW = "Europe/Moscow";

    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<ErrorResponse> handleRequestNotFound(TaskNotFoundException exception) {
        return Mono.just(ErrorResponse.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(exception.getMessage())
                .zonedDateTime(ZonedDateTime.now().withZoneSameInstant(ZoneId.of(EUROPE_MOSCOW)))
                .build());
    }

    @ExceptionHandler(TaskValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ErrorResponse> handleRequestNotFound(TaskValidationException exception) {
        return Mono.just(ErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(exception.getMessage())
                .zonedDateTime(ZonedDateTime.now().withZoneSameInstant(ZoneId.of(EUROPE_MOSCOW)))
                .build());
    }




}
