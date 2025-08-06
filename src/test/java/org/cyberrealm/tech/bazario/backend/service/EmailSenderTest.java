package org.cyberrealm.tech.bazario.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.mail.internet.MimeMessage;
import org.cyberrealm.tech.bazario.backend.service.impl.SmtpEmailSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class EmailSenderTest {
    @Mock
    private JavaMailSender mailSender;
    @InjectMocks
    private SmtpEmailSender sender;

    @Test
    void sendEmail() {
        ReflectionTestUtils.setField(sender, "fromEmail", "test@examle.com");
        var mockSender = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockSender);

        sender.sendEmail("user@examle.com", "test", "html");

        verify(mailSender).createMimeMessage();

        var messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        assertEquals(mockSender, messageCaptor.getValue());
    }
}
