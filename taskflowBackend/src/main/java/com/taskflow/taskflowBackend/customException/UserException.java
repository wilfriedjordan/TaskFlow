package com.taskflow.taskflowBackend.customException;


public class UserException extends Exception {
    public String code;

    public String getCode() {
        return code;
    }

    public UserException(String message, String code) {
        super(message);
        this.code = code;
    }
}
