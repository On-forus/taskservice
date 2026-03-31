package com.task.taskservice.persistense.repository;

import com.task.taskservice.dto.PageResponse;
import com.task.taskservice.dto.TaskStatus;
import com.task.taskservice.dto.UpdateTaskStatusDto;
import com.task.taskservice.exception.TaskNotFoundException;
import com.task.taskservice.persistense.entity.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TaskRepositoryImpl implements TaskRepository {

    private final JdbcClient jdbcClient;

    public Mono<Task> save(Task task) {

        return Mono.fromCallable(() -> jdbcClient.sql("""
                                INSERT INTO task (title, description, status, created_at, updated_at)
                                VALUES (?,?,?,?,?)
                                RETURNING *"""
                        )
                        .param(task.getTitle())
                        .param(task.getDescription())
                        .param(task.getStatus().toString())
                        .param(task.getCreatedAt())
                        .param(task.getUpdatedAt())
                        .query(Task.class)
                        .single()
        ).subscribeOn(Schedulers.boundedElastic());

    }

    @Override
    public Mono<PageResponse<Task>> getPageableListTask(Pageable pageable) {
        return Mono.fromCallable(() -> {
            Long total = jdbcClient.sql("""
                            SELECT COUNT(*) FROM task"""
                    ).query(Long.class)
                    .single();


            List<Task> tasks = jdbcClient.sql("""
                            SELECT  * From task
                            ORDER BY created_at DESC
                            LIMIT ? OFFSET ?""")
                    .param(pageable.getPageSize())
                    .param(pageable.getOffset())
                    .query(Task.class)
                    .list();

            return PageResponse.from(new PageImpl<>(tasks, pageable, total));
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Task> findById(long id) {
        return Mono.fromCallable(() -> {
            return jdbcClient.sql("""
                            SELECT * FROM task WHERE id = ?
                            """)
                    .param(id)
                    .query(Task.class)
                    .single();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Task> update(long id, UpdateTaskStatusDto updateStatus) {
        return Mono.fromCallable(() -> {
            return jdbcClient.sql("""
                            UPDATE task SET status = ?, updated_at = ? WHERE id = ?
                            RETURNING *""")
                    .param(updateStatus.status().toString())
                    .param(LocalDateTime.now())
                    .param(id)
                    .query(Task.class)
                    .single();
        }).subscribeOn(Schedulers.boundedElastic());

    }

    @Override
    public Mono<Void> removeById(long id) {
        return Mono.fromCallable(() -> {
            int count = jdbcClient.sql("""
                            DELETE FROM task WHERE id = ?
                            """)
                    .param(id)
                    .update();

            if(count == 0) {throw new TaskNotFoundException("Task with id: "+ id +" not found");}
             return count;
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}