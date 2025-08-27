package com.taskflow.taskflowBackend.dtos;

import lombok.Data;

@Data
public class UserRegisterDto {
    private String username;
    private String email;
    private String password;
}
