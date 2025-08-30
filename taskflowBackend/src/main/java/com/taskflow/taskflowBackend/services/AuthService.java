package com.taskflow.taskflowBackend.services;

import com.taskflow.taskflowBackend.customException.UserException;
import com.taskflow.taskflowBackend.dtos.ResetPasswordUserDto;
import com.taskflow.taskflowBackend.dtos.UserLoginDto;
import com.taskflow.taskflowBackend.dtos.UserPostLogin;
import com.taskflow.taskflowBackend.dtos.UserRegisterDto;
import com.taskflow.taskflowBackend.entity.User;
import com.taskflow.taskflowBackend.repositories.UserRepository;
import com.taskflow.taskflowBackend.utils.Constants;
import com.taskflow.taskflowBackend.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final EmailServiceSender emailServiceSender;

    private String regexEmail = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    private String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";

    public String register(UserRegisterDto userRegisterDto) throws UserException {

        if(userRepository.findByUsername(userRegisterDto.getUsername())!=null){
            throw new UserException(Constants.USER_ENTITY_ALREADY_EXISTS,Constants.USER_ENTITY_ALREADY_EXISTS_CODE);
        }
        if(userRegisterDto.getPassword().matches(passwordRegex) && userRegisterDto.getEmail().matches(regexEmail)){
            User user = new User();
            user.setUsername(userRegisterDto.getUsername());
            user.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));
            user.setEmail(userRegisterDto.getEmail());
            userRepository.save(user);
            emailServiceSender.emailWelcome(userRegisterDto.getEmail(),userRegisterDto.getUsername());
            return "Inscription Reussir";

        }else {
            throw new UserException(Constants.USER_ENTITY_BAD_REQUEST,Constants.USER_ENTITY_BAD_REQUEST_CODE);
        }

    }

    public UserPostLogin login(UserLoginDto userLoginDto) throws UserException {
        if (userRepository.findByUsername(userLoginDto.getUsername()) == null) {
            throw new UserException(Constants.USER_ENTITY_NOT_FOUND,Constants.USER_ENTITY_NOT_FOUND_CODE);
        }
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginDto.getUsername(), userLoginDto.getPassword()));
            UserPostLogin userPostLogin = new UserPostLogin();

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByUsername(userDetails.getUsername());
            userPostLogin.setToken(jwtUtils.generateToken(userDetails.getUsername()));
            userPostLogin.getDataPostLoginDto().setUsername(userDetails.getUsername());
            userPostLogin.getDataPostLoginDto().setRoles(userDetails.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList());
            userPostLogin.getDataPostLoginDto().setEmail(user.getEmail());
            return userPostLogin;

        }catch (Exception e){
            log.error(e.getMessage());
            throw new UserException(Constants.USER_ENTITY_UNAUTHORIZED,Constants.USER_ENTITY_UNAUTHORIZED_CODE);
        }
    }

    public String resetPasswordUser(ResetPasswordUserDto resetPasswordUserDto) throws UserException {

        Optional<User> userOptional = userRepository.findByEmail(resetPasswordUserDto.getEmail());
        if (userOptional.isPresent()) {
            if (resetPasswordUserDto.getNewPassword().matches(passwordRegex)){
                User user = userOptional.get();
                user.setPassword(passwordEncoder.encode(resetPasswordUserDto.getNewPassword()));
                userRepository.save(user);
                return "Password reset successful";
            }else {
                throw new UserException("Mot de passe invalide. Il doit contenir au minimum 8 caractères, au moins une majuscule,un caractère spécial et ne doit pas contenir d'espaces.",
                        Constants.USER_ENTITY_BAD_REQUEST_CODE);
            }
        }else{
            throw new UserException(Constants.USER_ENTITY_NOT_FOUND,Constants.USER_ENTITY_NOT_FOUND_CODE);
        }
    }
}
