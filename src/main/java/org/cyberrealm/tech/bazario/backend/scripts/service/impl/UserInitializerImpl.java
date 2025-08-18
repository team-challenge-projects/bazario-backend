package org.cyberrealm.tech.bazario.backend.scripts.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.script.UserCredentials;
import org.cyberrealm.tech.bazario.backend.mapper.UserMapper;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.cyberrealm.tech.bazario.backend.scripts.service.UserInitializer;
import org.cyberrealm.tech.bazario.backend.util.GeometryUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInitializerImpl implements UserInitializer {
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${user.root.credentials.email}")
    private String rootEmail;

    @Override
    public User getUser(UserCredentials credentials) {
        return userRepository.findByEmail(credentials.getEmail())
                .orElseGet(() -> getCurrentUser(credentials));
    }

    @Override
    public List<User> getUsers(List<UserCredentials> dtoUsers) {
        var users = userRepository.findByEmailIn(dtoUsers.stream()
                .map(UserCredentials::getEmail).toList());

        List<UserCredentials> notExistCredentials = getNotExistCredentials(dtoUsers, users);

        if (notExistCredentials.isEmpty()) {
            return users;
        }

        var newUsers = createUsers(notExistCredentials);
        return Stream.of(users, newUsers).flatMap(Collection::stream).toList();
    }

    private List<User> createUsers(List<UserCredentials> credentials) {
        List<User> users = credentials.stream().map(dto -> {
            var user = userMapper.toUser(dto);
            user.setPassword(encoder.encode(dto.getPassword()));
            user.setCityCoordinate(GeometryUtil.createPoint(dto.getCityCoordinate()));
            return user;
        }).toList();
        return userRepository.saveAll(users);
    }

    private List<UserCredentials> getNotExistCredentials(List<UserCredentials> dtoUsers,
                                                         List<User> users) {
        var emails = users.stream().map(User::getEmail).toList();
        return dtoUsers.stream().filter(dto ->
                !emails.contains(dto.getEmail())).toList();
    }

    private User getCurrentUser(UserCredentials credentials) {
        var currentUser = userMapper.toUser(credentials);
        currentUser.setPassword(encoder.encode(currentUser.getPassword()));
        return userRepository.save(currentUser);
    }
}
