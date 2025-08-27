package com.taskflow.taskflowBackend.services;

import com.taskflow.taskflowBackend.entity.User;
import com.taskflow.taskflowBackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userExisting = userRepository.findByUsername(username);
        if (userExisting == null) {
            throw new UsernameNotFoundException("L'utilisateur donc le username est "+ username + "n'existe pas");
        }
        return new org.springframework.security.core.userdetails.User(
                userExisting.getUsername(),
                userExisting.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(userExisting.getRole().name()))
        );
    }
}
