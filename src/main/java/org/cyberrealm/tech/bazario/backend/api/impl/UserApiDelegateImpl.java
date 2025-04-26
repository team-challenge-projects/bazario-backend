package org.cyberrealm.tech.bazario.backend.api.impl;

import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.api.UserApiDelegate;
import org.cyberrealm.tech.bazario.backend.dto.PatchUser;
import org.cyberrealm.tech.bazario.backend.dto.PrivateUserInformation;
import org.cyberrealm.tech.bazario.backend.dto.RegistrationRequest;
import org.cyberrealm.tech.bazario.backend.dto.TypeEmailMessage;
import org.cyberrealm.tech.bazario.backend.dto.UserInformation;
import org.cyberrealm.tech.bazario.backend.dto.VerificationEmail;
import org.cyberrealm.tech.bazario.backend.service.UserService;
import org.cyberrealm.tech.bazario.backend.service.impl.EmailNotificationService;
import org.cyberrealm.tech.bazario.backend.service.impl.EmailVerificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserApiDelegateImpl implements UserApiDelegate {
    private final EmailNotificationService notificationService;
    private final EmailVerificationService emailVerificationService;
    private final UserService userService;

    @Override
    public ResponseEntity<Void> createUser(RegistrationRequest registrationRequest) {
        userService.register(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> deleteUser() {
        return UserApiDelegate.super.deleteUser();
    }

    @Override
    public ResponseEntity<UserInformation> getOtherUserInformation(Long id) {
        return UserApiDelegate.super.getOtherUserInformation(id);
    }

    @Override
    public ResponseEntity<PrivateUserInformation> getUserInformation() {
        return UserApiDelegate.super.getUserInformation();
    }

    @Override
    public ResponseEntity<Void> sendMessage(TypeEmailMessage type, String body) {
        notificationService.sendNotification(type, body);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<UserInformation> updateUser(PatchUser patchUser) {
        return UserApiDelegate.super.updateUser(patchUser);
    }

    @Override
    public ResponseEntity<UserInformation> updateUserByAdmin(Long id, PatchUser patchUser) {
        return UserApiDelegate.super.updateUserByAdmin(id, patchUser);
    }

    @Override
    public ResponseEntity<String> verifyEmail(VerificationEmail verificationEmail) {
        boolean isValid = emailVerificationService.verifyToken(verificationEmail);
        if (!isValid) {
            return ResponseEntity.badRequest().build();
        }

        emailVerificationService.markVerified(verificationEmail.getEmail());
        return ResponseEntity.ok().build();
    }
}
