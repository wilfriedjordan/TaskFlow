package com.taskflow.taskflowBackend.customException;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorApi {
    private String message;
    private String code;
    private LocalDateTime timestamp;
    private String path;
}
