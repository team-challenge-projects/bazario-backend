package org.cyberrealm.tech.bazario.backend.security;

import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.config.RootUserCredentials;
import org.cyberrealm.tech.bazario.backend.mapper.UserMapper;
import org.cyberrealm.tech.bazario.backend.model.enums.Role;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RootUserInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RootUserCredentials credentials;
    private final UserMapper mapper;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByRole(Role.ROLE_ROOT)) {
            var root = mapper.toUser(credentials);
            root.setRole(Role.ROLE_ROOT);
            root.setPassword(encoder.encode(root.getPassword()));
            userRepository.save(root);
        }

    }
}
