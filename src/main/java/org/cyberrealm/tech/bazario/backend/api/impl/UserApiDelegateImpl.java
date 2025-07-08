package org.cyberrealm.tech.bazario.backend.api.impl;

import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.api.UserApiDelegate;
import org.cyberrealm.tech.bazario.backend.dto.EmailRequest;
import org.cyberrealm.tech.bazario.backend.dto.PatchUser;
import org.cyberrealm.tech.bazario.backend.dto.PrivateUserInformation;
import org.cyberrealm.tech.bazario.backend.dto.PublicUserInformation;
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
    public ResponseEntity<PublicUserInformation> getPublicUserInformation(Long id) {
        return ResponseEntity.ok(userService.getPublicInformationById(id));
    }

    @Override
    public ResponseEntity<RegistrationRequest> createUser(RegistrationRequest registrationRequest) {
        userService.register(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(registrationRequest);
    }

    @Override
    public ResponseEntity<Void> deleteUser() {
        userService.delete();
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteUserByAdmin(Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<UserInformation> getOtherUserInformation(Long id) {
        return ResponseEntity.ok(userService.getInformationById(id));
    }

    @Override
    public ResponseEntity<PrivateUserInformation> getUserInformation() {
        return ResponseEntity.ok(userService.getInformation());
    }

    @Override
    public ResponseEntity<Void> sendMessage(TypeEmailMessage type, EmailRequest emailRequest) {
        notificationService.sendNotification(type, emailRequest.getEmail());
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<PrivateUserInformation> updateUser(PatchUser patchUser) {
        return ResponseEntity.ok(userService.update(patchUser));
    }

    @Override
    public ResponseEntity<PrivateUserInformation> updateUserByAdmin(Long id, PatchUser patchUser) {
        return ResponseEntity.ok(userService.updateById(id, patchUser));
    }

    @Override
    public ResponseEntity<Void> verifyEmail(VerificationEmail verificationEmail) {
        boolean isValid = emailVerificationService.verifyToken(verificationEmail);
        if (!isValid) {
            return ResponseEntity.badRequest().build();
        }

        emailVerificationService.markVerified(verificationEmail.getEmail());
        return ResponseEntity.noContent().build();
    }
}
