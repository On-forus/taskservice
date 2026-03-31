package com.task.taskservice.persistense.entity;

import com.task.taskservice.dto.TaskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;


import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @NotNull
    @Column(name = "title", nullable = false)
    private String title;
    @NotNull
    @Column(name = "description", nullable = false)
    private String description;
    @NotNull
    @Column(name = "status", nullable = false)
    private TaskStatus status;
    @NotNull
    @Column(name = "createdAt", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;
    @NotNull
    @Column(name = "updatedAt", nullable = false)
    private LocalDateTime updatedAt;
}
