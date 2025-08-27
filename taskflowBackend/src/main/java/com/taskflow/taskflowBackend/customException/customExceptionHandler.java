package com.taskflow.taskflowBackend.customException;

import com.taskflow.taskflowBackend.utils.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;


@ControllerAdvice
public class customExceptionHandler {


    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorApi> handleUserException(UserException e, WebRequest request) {
        ErrorApi apiError = new ErrorApi();
        apiError.setMessage(e.getMessage());
        apiError.setCode(e.getCode());
        apiError.setTimestamp(LocalDateTime.now());
        apiError.setPath(getPath(request));

        HttpStatus status =mapCodeToHttpStatus(e.getCode());
        return ResponseEntity.status(status).body(apiError);
    }

    private HttpStatus mapCodeToHttpStatus(String code) {
        return  switch (code){
            case Constants.USER_ENTITY_ALREADY_EXISTS_CODE -> HttpStatus.CONFLICT;
            case Constants.USER_ENTITY_NOT_FOUND_CODE ->  HttpStatus.NOT_FOUND;
            case Constants.USER_ENTITY_UNAUTHORIZED_CODE ->  HttpStatus.UNAUTHORIZED;
            case Constants.USER_ENTITY_BAD_REQUEST_CODE -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }


    public String getPath(WebRequest webRequest) {
        return webRequest.getDescription(false).replace("uri","");
    }
}
