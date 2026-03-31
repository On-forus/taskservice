package com.task.taskservice.service;

import com.task.taskservice.dto.CreateTaskDto;
import com.task.taskservice.dto.PageResponse;
import com.task.taskservice.dto.UpdateTaskStatusDto;
import com.task.taskservice.persistense.entity.Task;
import reactor.core.publisher.Mono;

public interface TaskService {
    Mono<Task> createTask(Mono<CreateTaskDto> createTaskDto);
    Mono<PageResponse<Task>> getPageableListTask(int page, int size);
    Mono<Task> getTaskById(long id);
    Mono<Task> updateTaskStatus(long id, UpdateTaskStatusDto updateTaskStatusDto);
    Mono<Void> deleteTaskById(long id);
}
