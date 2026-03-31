package com.task.taskservice.persistense.repository;

import com.task.taskservice.dto.TaskStatus;
import com.task.taskservice.dto.UpdateTaskStatusDto;
import com.task.taskservice.persistense.entity.Task;
import reactor.core.publisher.Mono;

public interface CRUDRepository<T>{
    Mono<T> save(T task);
    Mono<T> findById(long id);
    Mono<T> update(long id, UpdateTaskStatusDto updateStatus);

    Mono<Void> removeById(long id);
}
