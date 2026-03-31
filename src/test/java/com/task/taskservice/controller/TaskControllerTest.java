package com.task.taskservice.controller;

import com.task.taskservice.dto.CreateTaskDto;
import com.task.taskservice.dto.PageResponse;
import com.task.taskservice.dto.TaskStatus;
import com.task.taskservice.dto.UpdateTaskStatusDto;
import com.task.taskservice.persistense.entity.Task;
import com.task.taskservice.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = TaskController.class)
@AutoConfigureWebTestClient
class TaskControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private TaskService taskService;


    @Test
    void createTask() {
        Long taskId = 1L;
        LocalDateTime now = LocalDateTime.now();
        CreateTaskDto request = new CreateTaskDto("IGA", "Dynamic");
        Task task = new Task(taskId, "IGA", "Dynamic", TaskStatus.NEW, now, now);

        when(taskService.createTask(any())).thenReturn(Mono.just(task));

        webTestClient
                .post()
                .uri("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(taskId)
                .jsonPath("$.title").isEqualTo("IGA")
                .jsonPath("$.description").isEqualTo("Dynamic")
                .jsonPath("$.status").isEqualTo(TaskStatus.NEW.name())
                .jsonPath("$.createdAt").isEqualTo(now)
                .jsonPath("$.updatedAt").isEqualTo(now);
    }

    @Test
    void getTask_ReturnStatusOk() {
        Long taskId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Task task = new Task(taskId, "IGA", "Dynamic", TaskStatus.NEW, now, now);

        when(taskService.getTaskById(eq(taskId))).thenReturn(Mono.just(task));

        webTestClient
                .get()
                .uri("/api/tasks/{id}", taskId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(taskId)
                .jsonPath("$.title").isEqualTo("IGA")
                .jsonPath("$.description").isEqualTo("Dynamic")
                .jsonPath("$.status").isEqualTo(TaskStatus.NEW.name())
                .jsonPath("$.createdAt").isEqualTo(now)
                .jsonPath("$.updatedAt").isEqualTo(now);
    }

    @Test
    void getTask_ReturnStatusNotFound() {
        Long taskId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Task task = new Task(taskId, "IGA", "Dynamic", TaskStatus.NEW, now, now);

        when(taskService.getTaskById(eq(taskId))).thenReturn(Mono.empty());

        webTestClient
                .get()
                .uri("/api/tasks/{id}", taskId)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .isEmpty();
    }

    @Test
    void updateTaskStatus() {
        Long taskId = 1L;
        LocalDateTime now = LocalDateTime.now();
        UpdateTaskStatusDto updateTaskStatusDto = new UpdateTaskStatusDto(TaskStatus.DONE);
        Task taskResponse = new Task(taskId, "IGA", "Dynamic", TaskStatus.DONE, now, now);

        when(taskService.updateTaskStatus(eq(taskId), eq(updateTaskStatusDto))).thenReturn(Mono.just(taskResponse));

        webTestClient
                .patch()
                .uri("/api/tasks/{id}/status", taskId, updateTaskStatusDto)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateTaskStatusDto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(taskId)
                .jsonPath("$.title").isEqualTo("IGA")
                .jsonPath("$.description").isEqualTo("Dynamic")
                .jsonPath("$.status").isEqualTo(taskResponse.getStatus())
                .jsonPath("$.createdAt").isEqualTo(now)
                .jsonPath("$.updatedAt").isEqualTo(now);
    }

    @Test
    void deleteTaskById() {
        Long taskId = 1L;
        when(taskService.deleteTaskById(taskId)).thenReturn(Mono.empty());

        webTestClient
                .delete()
                .uri("/api/tasks/{id}", taskId)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody()
                .isEmpty();
    }

    @Test
    void getListOfTasksAndPagination() { //todo add filter
        int size = 0;
        int page = 0;
        LocalDateTime now = LocalDateTime.now();
        List<Task> tasks = List.of(
                new Task(1L, "IGA", "Dynamic", TaskStatus.DONE, now, now),
                new Task(2L, "Stay", "Description Stay", TaskStatus.NEW, now, now)
        );
        int taskSize = tasks.size();
        double totalPages = Math.ceil(((double) taskSize / (double) size));

        PageResponse<Task> pageResponse = new PageResponse<>(tasks, page, size, taskSize, (int) totalPages);

        when(taskService.getPageableListTask(eq(page), eq(size))).thenReturn(Mono.just(pageResponse));

        webTestClient
                .get()
                .uri("/api/tasks?page={page}&size={size}", page, size)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.content[0].id").isEqualTo(1L)
                .jsonPath("$.content[0].title").isEqualTo("IGA")
                .jsonPath("$.content[0].description").isEqualTo("Dynamic")
                .jsonPath("$.content[0].status").isEqualTo(TaskStatus.DONE.name())
                .jsonPath("$.content[0].createdAt").isEqualTo(now)
                .jsonPath("$.content[0].updatedAt").isEqualTo(now)
                .jsonPath("$.page").isEqualTo(page)
                .jsonPath("$.size").isEqualTo(size)
                .jsonPath("$.totalElements").isEqualTo(taskSize)
                .jsonPath("$.totalPages").isEqualTo(totalPages);

    }

}