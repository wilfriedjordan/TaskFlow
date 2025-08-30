package com.taskflow.taskflowBackend.dtos;

import lombok.Data;

import java.util.List;

@Data
public class UserPostLogin {
    private String token;

    private String username;
    private String email;
    private List<String> roles;
}
