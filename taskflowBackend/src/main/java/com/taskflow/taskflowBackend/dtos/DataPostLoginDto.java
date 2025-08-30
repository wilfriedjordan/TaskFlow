package com.taskflow.taskflowBackend.dtos;

import lombok.Data;

import java.util.List;


@Data
public class DataPostLoginDto {
    private String username;
    private String email;
    private List<String> roles;
}
