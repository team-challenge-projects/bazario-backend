package org.cyberrealm.tech.bazario.backend.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.exception.EmailProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {
    public static final String UTF_8 = "UTF-8";
    public static final String EXPIRATION_MINUTES = "expirationMinutes";
    public static final String RESET_LINK = "resetLink";
    public static final String TEMPLATE = "reset-password";
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendPasswordResetEmail(String toEmail, String subject,
                                       int expirationMinutes, String resetLink) {
        Context context = new Context();
        context.setVariable(EXPIRATION_MINUTES, expirationMinutes);
        context.setVariable(RESET_LINK, resetLink);

        String htmlContent = templateEngine.process(TEMPLATE, context);

        sendEmail(toEmail, subject, htmlContent);
    }

    private void sendEmail(String toEmail, String subject, String htmlContent) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true,
                    UTF_8);
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailProcessingException("Can't send email", e);
        }
    }
}

