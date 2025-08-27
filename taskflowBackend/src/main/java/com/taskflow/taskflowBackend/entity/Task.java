package com.taskflow.taskflowBackend.entity;


import com.taskflow.taskflowBackend.Enums.PriorityTask;
import com.taskflow.taskflowBackend.Enums.TaskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@RequiredArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.TODO;
    @Enumerated(EnumType.STRING)
    private PriorityTask priority;
    private LocalDateTime dueDate;


    @ManyToOne
    @JoinColumn(name = "project_id",nullable = false)
    private Project project;

    @OneToMany(mappedBy = "task" ,cascade = CascadeType.ALL)
    private Set<Comment> comments;

    @ManyToMany(mappedBy = "tasks")
    private Set<User> users;

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }

}
