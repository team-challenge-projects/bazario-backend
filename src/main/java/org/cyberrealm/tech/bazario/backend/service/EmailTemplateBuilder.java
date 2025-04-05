package org.cyberrealm.tech.bazario.backend.service;

public interface EmailTemplateBuilder {
    String buildPasswordResetEmail(int expirationMinutes, String resetLink);

    String buildEmailVerificationEmail(int expirationHours, String verificationLink);
}
