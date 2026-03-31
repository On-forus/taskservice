package com.task.taskservice.service;

import com.task.taskservice.dto.CreateTaskDto;
import com.task.taskservice.dto.PageResponse;
import com.task.taskservice.dto.TaskStatus;
import com.task.taskservice.dto.UpdateTaskStatusDto;
import com.task.taskservice.exception.TaskNotFoundException;
import com.task.taskservice.exception.TaskValidationException;
import com.task.taskservice.persistense.entity.Task;
import com.task.taskservice.persistense.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TransactionTemplate transactionTemplate;

    @Override
    public Mono<Task> createTask(Mono<CreateTaskDto> createTaskDto) {
        return createTaskDto.flatMap((request) -> {
            log.info("Create task with title: {}", request.title());
            return transactionTemplate.execute(action -> {
                return taskRepository.save(Task.builder()
                        .title(request.title())
                        .description(request.description())
                        .status(TaskStatus.NEW)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build());
            });

        }).onErrorMap(RuntimeException.class, exception ->
                new TaskValidationException("Create task request parameters not valid"));
        // todo Заменить текст исключения на более понятный. Какая конкретно ошибка валидации и у какого значения
    }


    @Override
    public Mono<PageResponse<Task>> getPageableListTask(int page, int size) {
        return Mono.defer(() -> {
            return transactionTemplate.execute(action -> {
                try {
                    return taskRepository.getPageableListTask(PageRequest.of(page, size));
                } catch (RuntimeException e) {
                    action.setRollbackOnly();
                    log.error(e.getMessage());
                    return Mono.error(e);
                }
            });
        }).onErrorMap(IllegalArgumentException.class, e ->
            new TaskValidationException(e.getMessage())
        );
    }

    @Override
    public Mono<Task> getTaskById(long id) {
        return Mono.defer(() -> {
            return transactionTemplate.execute(action -> {
                return taskRepository.findById(id);
            });
        }).onErrorMap(EmptyResultDataAccessException.class, e ->
                new TaskNotFoundException("Task not found with id: " + id));
    }

    @Override
    public Mono<Task> updateTaskStatus(long id, UpdateTaskStatusDto updateTaskStatusDto) {
        return Mono.defer(() -> {
            return transactionTemplate.execute(action -> {
                return taskRepository.update(id, updateTaskStatusDto);
            });
        }).onErrorMap(EmptyResultDataAccessException.class, e ->
                new TaskNotFoundException("Task not found with id: " + id)
        ).onErrorMap(IllegalArgumentException.class, e ->
                new TaskValidationException(e.getMessage()));
    }

    @Override
    public Mono<Void> deleteTaskById(long id) {
        return Mono.defer(() -> {
            return transactionTemplate.execute(action -> {
                return taskRepository.removeById(id);
            });
        }).onErrorMap(EmptyResultDataAccessException.class, e ->
                new TaskNotFoundException("Task not found with id: " + id)
        );
    }
}