package org.cyberrealm.tech.bazario.backend.service;

public interface EmailTemplateBuilder {
    /**
     * Build HTML page reset password and convert to string
     *
     * @author Vitalii Pavlyk
     * @param expirationMinutes Minutes of expiration active link
     * @param resetLink Link reset password
     * @return HTML page convert to string
     */
    String buildPasswordResetEmail(int expirationMinutes, String resetLink);

    /**
     * Build HTML page verification email and convert to string
     *
     * @author Vitalii Pavlyk
     * @param expirationHours Hours of expiration active link
     * @param verificationLink Link verify email
     * @return HTML page convert to string
     */
    String buildEmailVerificationEmail(int expirationHours, String verificationLink);
}
