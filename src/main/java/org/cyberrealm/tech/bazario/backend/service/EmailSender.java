package org.cyberrealm.tech.bazario.backend.service;

public interface EmailSender {
    /**
     * Send email
     *
     * @author Vitalii Pavlyk
     * @param toEmail Email to which the message is sent
     * @param subject Title mail
     * @param content HTML page convert string
     */
    void sendEmail(String toEmail, String subject, String content);
}
