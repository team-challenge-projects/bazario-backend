package org.cyberrealm.tech.bazario.backend.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.cyberrealm.tech.bazario.backend.service.impl.ThymeleafEmailTemplateBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class EmailTemplateBuilderTest {
    @Mock
    private TemplateEngine templateEngine;
    @InjectMocks
    private ThymeleafEmailTemplateBuilder builder;

    @Test
    void buildPasswordResetEmail() {
        when(templateEngine.process(eq("reset-password"), any(Context.class)))
                .thenReturn("html");

        builder.buildPasswordResetEmail(1000, "link");

        var contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq("reset-password"), contextCaptor.capture());

        var context = contextCaptor.getValue();
        assertTrue(context.containsVariable("expirationMinutes"));
        assertTrue(context.containsVariable("resetLink"));
    }

    @Test
    void buildEmailVerificationEmail() {
        when(templateEngine.process(eq("email-verification"), any(Context.class)))
                .thenReturn("html");

        builder.buildEmailVerificationEmail(1000, "link");

        var contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq("email-verification"), contextCaptor.capture());

        var context = contextCaptor.getValue();
        assertTrue(context.containsVariable("expirationMinutes"));
        assertTrue(context.containsVariable("verificationLink"));
    }
}
