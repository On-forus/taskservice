package com.task.taskservice.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateTaskStatusDto(
        @NotNull

        TaskStatus status
) {
}
