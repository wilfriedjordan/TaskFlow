package com.taskflow.taskflowBackend.repositories;

import com.taskflow.taskflowBackend.entity.OtpEmailUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpEmailUserRepository extends JpaRepository<OtpEmailUser, Long> {
    OtpEmailUser findOtpEmailUserByEmailAndCode(String email, String code);
}
