package org.cyberrealm.tech.bazario.backend.service;

public interface EmailSender {
    void sendEmail(String toEmail, String subject, String content);
}
