package com.task.taskservice.controller;

import com.task.taskservice.dto.CreateTaskDto;
import com.task.taskservice.dto.PageResponse;
import com.task.taskservice.dto.UpdateTaskStatusDto;
import com.task.taskservice.persistense.entity.Task;
import com.task.taskservice.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
@Validated
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public Mono<ResponseEntity<Task>> createTask(@RequestBody @Valid Mono<CreateTaskDto> createTaskDto) {
        return taskService.createTask(createTaskDto).map((task) -> {
            log.info("Task created, id: {}", task.getId());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(task);
        });
    }

    @GetMapping
    public Mono<ResponseEntity<PageResponse<Task>>> getTasks(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Pageable request, page: {}, size: {}", page, size);
        //todo фильтрация по status
        return taskService.getPageableListTask(page, size).map(pageResponse -> {
            log.info("Pageable response, page: {}, size: {}, total: {}", pageResponse.page(), pageResponse.size(), pageResponse.totalElements());

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(pageResponse);
        });
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<Task>> getTaskById(@PathVariable long id) {
        log.info("Getting task with id: {}", id);

        return taskService.getTaskById(id).map(getResponse ->

                ResponseEntity
                        .status(HttpStatus.OK)
                        .body(getResponse)).defaultIfEmpty(ResponseEntity.notFound().build()).subscribeOn(Schedulers.boundedElastic());
    }

    @PatchMapping("{id}/status")
    public Mono<ResponseEntity<Task>> updateTaskStatus(@PathVariable long id,
                                                       @RequestBody @Valid UpdateTaskStatusDto taskStatus) {

        log.info("Task update request, id: {}, status: {}", id, taskStatus.status().toString());

        return taskService.updateTaskStatus(id, taskStatus).map(updateResponse ->
                ResponseEntity
                        .status(HttpStatus.OK)
                        .body(updateResponse));
    }

    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Void>> deleteTaskById(@PathVariable long id) {

        log.info("Task deleted request, id: {}", id);
        return taskService.deleteTaskById(id)
                .then(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .build()));
    }


}
