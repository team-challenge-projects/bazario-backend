package org.cyberrealm.tech.bazario.backend.service.impl;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.UserRegistrationRequestDto;
import org.cyberrealm.tech.bazario.backend.dto.UserResponseDto;
import org.cyberrealm.tech.bazario.backend.exception.RegistrationException;
import org.cyberrealm.tech.bazario.backend.mapper.UserMapper;
import org.cyberrealm.tech.bazario.backend.model.Role;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.repository.RoleRepository;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.cyberrealm.tech.bazario.backend.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private static final Role.RoleName DEFAULT_ROLE = Role.RoleName.ROLE_USER;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Transactional
    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        User user = userMapper.toModel(requestDto);
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RegistrationException("User with email: " + user.getEmail()
                    + " already exists");
        }
        Role role = roleRepository.findByRole(DEFAULT_ROLE).orElseThrow(
                () -> new RegistrationException("Role: " + DEFAULT_ROLE + " not found")
        );
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(role));
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }
}
