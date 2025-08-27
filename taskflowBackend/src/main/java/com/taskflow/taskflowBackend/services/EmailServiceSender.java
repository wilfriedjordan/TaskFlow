package com.taskflow.taskflowBackend.services;

import com.taskflow.taskflowBackend.customException.UserException;
import com.taskflow.taskflowBackend.dtos.OtpRequestSend;
import com.taskflow.taskflowBackend.dtos.VerifyOtpDto;
import com.taskflow.taskflowBackend.entity.OtpEmailUser;
import com.taskflow.taskflowBackend.entity.User;
import com.taskflow.taskflowBackend.repositories.OtpEmailUserRepository;
import com.taskflow.taskflowBackend.repositories.UserRepository;
import com.taskflow.taskflowBackend.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class EmailServiceSender {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final OtpEmailUserRepository otpEmailUserRepository;

    public void emailWelcome(String email, String username) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("🎉 Bienvenue dans TaskFlow - Votre espace de productivité !");
        message.setText("Bonjour " + username + ",\n\n" +
                "Nous sommes ravis de vous accueillir parmi nous sur TaskFlow 🚀 !\n\n" +
                "Avec TaskFlow, vous allez pouvoir :\n" +
                "✅ Créer et organiser vos projets facilement\n" +
                "✅ Suivre vos tâches et vos priorités en temps réel\n" +
                "✅ Collaborer efficacement avec votre équipe\n\n" +
                "Votre aventure de productivité commence dès maintenant.\n" +
                "Connectez-vous et commencez à organiser vos projets : http://localhost:4200/login\n\n" +
                "À très bientôt dans TaskFlow,\n" +
                "L’équipe TaskFlow ✨");

        mailSender.send(message);
    }

    public void otpSendToEmail(String email, String codeOtp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("🔐 Code de vérification pour la modification de votre mot de passe");

        message.setText("Bonjour,\n\n" +
                "Vous avez demandé à modifier votre mot de passe.\n" +
                "Veuillez utiliser le code de vérification ci-dessous pour confirmer votre identité :\n\n" +
                "👉 Code OTP : " + codeOtp + "\n\n" +
                "⚠️ Attention : Ce code est valable uniquement pendant 5 minutes. " +
                "Ne le partagez avec personne pour des raisons de sécurité.\n\n" +
                "Si vous n’êtes pas à l’origine de cette demande, ignorez simplement cet email.\n\n" +
                "Merci pour votre confiance,\n" +
                "L’équipe TaskFlow 🔒");

        mailSender.send(message);
    }

    public String sendCodeOtpToUser_thenEmailIs(OtpRequestSend otpRequestSend) throws UserException {

        Optional<User> userOptional = userRepository.findByEmail(otpRequestSend.getEmail());
        if (userOptional.isEmpty()) {
            throw new UserException(Constants.USER_ENTITY_NOT_FOUND,Constants.USER_ENTITY_NOT_FOUND_CODE);
        }else {
            String codeOtp = String.format("%06d", new Random().nextInt(999999));

            OtpEmailUser otpEmailUser = new OtpEmailUser();
            otpEmailUser.setCode(codeOtp);
            otpEmailUser.setEmail(otpRequestSend.getEmail());
            otpEmailUser.setExpirationTime(LocalDateTime.now().plusMinutes(5));
            otpEmailUserRepository.save(otpEmailUser);
            otpSendToEmail(otpRequestSend.getEmail(), codeOtp);
            return " Code OTP envoyé à votre adresse" + otpRequestSend.getEmail();
        }
    }

    public String verifyOtp(VerifyOtpDto verifyOtpDto) throws UserException {
        OtpEmailUser otpEmailUser = otpEmailUserRepository.findOtpEmailUserByEmailAndCode(verifyOtpDto.getEmail(), verifyOtpDto.getCodeOtp());
        if (otpEmailUser == null) {
            throw new UserException("Code OTP invalide",Constants.USER_ENTITY_BAD_REQUEST_CODE);

        }else {
            if (otpEmailUser.getExpirationTime().isBefore(LocalDateTime.now())) {
                throw new UserException("Code OTP expiré.",Constants.USER_ENTITY_BAD_REQUEST_CODE);
            }
            otpEmailUser.setUsed(true);
            otpEmailUserRepository.save(otpEmailUser);
            return "Code OTP validé .";
        }

    }


}
