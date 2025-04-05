package org.cyberrealm.tech.bazario.backend.service.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.model.enums.MessageType;
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

    @Value("${frontend.reset.password.url}")
    private String frontendResetPasswordUrl;

    @Value("${frontend.email.verification.url}")
    private String frontendEmailVerificationUrl;

    @Value("${token.expiration.minutes:15}")
    private int expirationMinutes;

    public void sendNotification(String messageType, String email) {
        if (MessageType.PASSWORD_RESET.name().equalsIgnoreCase(messageType)) {
            sendPasswordResetEmail(email);
        } else if (MessageType.EMAIL_VERIFICATION.name().equalsIgnoreCase(messageType)) {
            sendEmailVerification(email);
        } else {
            throw new IllegalArgumentException("Unsupported message type: " + messageType);
        }
    }

    private void sendPasswordResetEmail(String email) {
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
