package com.taskflow.taskflowBackend.Controllers;


import com.taskflow.taskflowBackend.customException.UserException;
import com.taskflow.taskflowBackend.dtos.*;
import com.taskflow.taskflowBackend.services.AuthService;
import com.taskflow.taskflowBackend.services.EmailServiceSender;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1.0/auths")
@RequiredArgsConstructor
public class UserAuthCtrl {
    private final AuthService authService;
    private final EmailServiceSender emailServiceSender;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegisterDto user) throws UserException {
        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.TEXT_PLAIN).body(authService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<UserPostLogin> loginUser(@RequestBody UserLoginDto user) throws UserException {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(user));
    }

    @PostMapping("/send-code")
    public ResponseEntity<String> sendCodeOtp(@RequestBody OtpRequestSend otpRequestSend) throws UserException {
        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.TEXT_PLAIN).body(emailServiceSender.sendCodeOtpToUser_thenEmailIs(otpRequestSend));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCodeOtp(@RequestBody VerifyOtpDto verifyOtpDto) throws UserException {
        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.TEXT_PLAIN).body(emailServiceSender.verifyOtp(verifyOtpDto));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordUserDto resetPasswordUserDto) throws UserException {
        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.TEXT_PLAIN).body(authService.resetPasswordUser(resetPasswordUserDto));
    }
}
