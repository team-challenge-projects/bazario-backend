package org.cyberrealm.tech.bazario.backend.scripts.service.impl;

import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.script.UserCredentials;
import org.cyberrealm.tech.bazario.backend.mapper.UserMapper;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.cyberrealm.tech.bazario.backend.scripts.service.UserInitializer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInitializerImpl implements UserInitializer {
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    @Override
    public User getUser(UserCredentials credentials) {
        return userRepository.findByEmail(credentials.getEmail())
                .orElseGet(() -> getCurrentUser(credentials));
    }

    private User getCurrentUser(UserCredentials credentials) {
        var currentUser = userMapper.toUser(credentials);
        currentUser.setPassword(encoder.encode(currentUser.getPassword()));
        return userRepository.save(currentUser);
    }
}
