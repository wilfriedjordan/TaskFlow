package com.taskflow.taskflowBackend.entity;


import com.taskflow.taskflowBackend.Enums.RoleUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Table(name = "users")
@Entity
@Data
@RequiredArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String username;
    @NotBlank
    private String email;
    @NotNull
    private String password;

    @Enumerated(EnumType.STRING)
    private RoleUser role = RoleUser.ROLE_USER;
    private boolean isActive = true;


    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private Set<Comment> comments;

    @OneToMany(mappedBy = "manager" ,cascade = CascadeType.ALL)
    private Set<Project> projects;

    @ManyToMany
    @JoinTable(
            name = "user_task",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id")
    )
    private Set<Task> tasks;

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
