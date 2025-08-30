package com.taskflow.taskflowBackend.dtos;

import lombok.Data;

import java.util.List;

@Data
public class UserPostLogin {
    private String token;
    private DataPostLoginDto dataPostLoginDto;
}
