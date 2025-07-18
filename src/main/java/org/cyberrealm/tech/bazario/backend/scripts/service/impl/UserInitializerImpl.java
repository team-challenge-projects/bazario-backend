package org.cyberrealm.tech.bazario.backend.scripts.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.script.UserCredentials;
import org.cyberrealm.tech.bazario.backend.mapper.UserMapper;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.cyberrealm.tech.bazario.backend.scripts.service.UserInitializer;
import org.cyberrealm.tech.bazario.backend.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInitializerImpl implements UserInitializer {
    private static final String DELIMITER_COORDINATE = "\\|";
    private static final int INDEX_FIRST_COORDINATE = 0;
    private static final int INDEX_NEXT_COORDINATE = 1;

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
        addCitiesInRedis(users);
        List<UserCredentials> notExistCredentials = getNotExistCredentials(dtoUsers, users);

        if (notExistCredentials.isEmpty()) {
            return users;
        }

        var newUsers = createUsers(notExistCredentials);
        return Stream.of(users, newUsers).flatMap(Collection::stream).toList();
    }

    private void addCitiesInRedis(List<User> users) {
        var allUsers = new ArrayList<>(users);
        allUsers.add(userRepository.findByEmail(rootEmail).orElseThrow());
        var notExistsCities = allUsers.stream().filter(user -> redisTemplate.opsForZSet()
                        .score(UserService.CITIES, user.getCityName()) == null)
                .collect(Collectors.toMap(User::getCityName, User::getCityCoordinate,
                        (existing, replacement) -> existing));
        notExistsCities.forEach((key, value) -> {
            var arrayCoordinate = value.split(DELIMITER_COORDINATE);
            redisTemplate.opsForGeo().add(UserService.CITIES,
                    new Point(Double.parseDouble(arrayCoordinate[INDEX_FIRST_COORDINATE]),
                            Double.parseDouble(arrayCoordinate[INDEX_NEXT_COORDINATE])),
                    key);
        });
    }

    private List<User> createUsers(List<UserCredentials> credentials) {
        List<User> users = credentials.stream().map(dto -> {
            var user = userMapper.toUser(dto);
            user.setPassword(encoder.encode(dto.getPassword()));
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
