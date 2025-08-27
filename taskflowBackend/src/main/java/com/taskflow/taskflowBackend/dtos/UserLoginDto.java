package com.taskflow.taskflowBackend.dtos;

import lombok.Data;

@Data
public class UserLoginDto {
    private String username;
    private String password;
}
