package org.cyberrealm.tech.bazario.backend.service.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.TypeEmailMessage;
import org.cyberrealm.tech.bazario.backend.exception.custom.EntityNotFoundException;
import org.cyberrealm.tech.bazario.backend.model.enums.MessageType;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.cyberrealm.tech.bazario.backend.service.EmailSender;
import org.cyberrealm.tech.bazario.backend.service.EmailTemplateBuilder;
import org.cyberrealm.tech.bazario.backend.service.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailNotificationService {
    private static final String EMAIL_PARAM = "?email=";
    private static final String TOKEN_PARAM = "&token=";
    private static final String PASSWORD_RESET = "Password Reset";
    private static final String EMAIL_VERIFICATION = "Email Verification";

    private final EmailSender emailSender;
    private final EmailTemplateBuilder templateBuilder;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Value("${frontend.reset.password.url}")
    private String frontendResetPasswordUrl;

    @Value("${frontend.email.verification.url}")
    private String frontendEmailVerificationUrl;

    @Value("${token.expiration.minutes:15}")
    private int expirationMinutes;

    public void sendNotification(TypeEmailMessage messageType, String email) {
        if (messageType.equals(TypeEmailMessage.RESET)) {
            sendPasswordResetEmail(email);
        } else if (messageType.equals(TypeEmailMessage.VERIFY)) {
            sendEmailVerification(email);
        } else {
            throw new IllegalArgumentException("Unsupported message type: " + messageType);
        }
    }

    private void sendPasswordResetEmail(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new EntityNotFoundException("User by email %s not found"
                    .formatted(email));
        }
        String token = tokenService.generateToken(email, MessageType.PASSWORD_RESET);
        String resetLink = frontendResetPasswordUrl + EMAIL_PARAM + email + TOKEN_PARAM
                + URLEncoder.encode(token, StandardCharsets.UTF_8);
        String htmlContent = templateBuilder.buildPasswordResetEmail(expirationMinutes, resetLink);
        emailSender.sendEmail(email, PASSWORD_RESET, htmlContent);
    }

    private void sendEmailVerification(String email) {
        String token = tokenService.generateToken(email, MessageType.EMAIL_VERIFICATION);
        String verificationLink = frontendEmailVerificationUrl + EMAIL_PARAM + email + TOKEN_PARAM
                + URLEncoder.encode(token, StandardCharsets.UTF_8);
        String htmlContent = templateBuilder.buildEmailVerificationEmail(expirationMinutes,
                verificationLink);
        emailSender.sendEmail(email, EMAIL_VERIFICATION, htmlContent);
    }
}
