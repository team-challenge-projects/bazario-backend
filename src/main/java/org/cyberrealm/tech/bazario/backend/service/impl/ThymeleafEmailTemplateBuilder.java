package org.cyberrealm.tech.bazario.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.service.EmailTemplateBuilder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class ThymeleafEmailTemplateBuilder implements EmailTemplateBuilder {
    private static final String EXPIRATION_MINUTES_CONTEXT = "expirationMinutes";
    private static final String RESET_LINK_CONTEXT = "resetLink";
    private static final String RESET_PASSWORD = "reset-password";
    private static final String VERIFICATION_LINK_CONTEXT = "verificationLink";
    private static final String EMAIL_VERIFICATION_CONTEXT = "email-verification";
    private final TemplateEngine templateEngine;

    @Override
    public String buildPasswordResetEmail(int expirationMinutes, String resetLink) {
        Context context = new Context();
        context.setVariable(EXPIRATION_MINUTES_CONTEXT, expirationMinutes);
        context.setVariable(RESET_LINK_CONTEXT, resetLink);
        return templateEngine.process(RESET_PASSWORD, context);
    }

    @Override
    public String buildEmailVerificationEmail(int expirationHours, String verificationLink) {
        Context context = new Context();
        context.setVariable(EXPIRATION_MINUTES_CONTEXT, expirationHours);
        context.setVariable(VERIFICATION_LINK_CONTEXT, verificationLink);
        return templateEngine.process(EMAIL_VERIFICATION_CONTEXT, context);
    }
}
