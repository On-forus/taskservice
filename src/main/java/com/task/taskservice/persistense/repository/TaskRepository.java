package com.task.taskservice.persistense.repository;

import com.task.taskservice.dto.PageResponse;
import com.task.taskservice.persistense.entity.Task;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface TaskRepository extends CRUDRepository<Task> {

    Mono<PageResponse<Task>> getPageableListTask(Pageable pageRequest);
}
