package org.cyberrealm.tech.bazario.backend.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.UUID;
import org.cyberrealm.tech.bazario.backend.model.enums.MessageType;
import org.cyberrealm.tech.bazario.backend.service.impl.DefaultTokenService;
import org.cyberrealm.tech.bazario.backend.service.impl.EncryptionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    public static final String EMAIL = "test@example.com";
    public static final String DELIMITER = ":";
    public static final String CODE = "000000";
    public static final String CODE_DECRYPT = "code";
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EncryptionUtils encryptionUtils;
    @InjectMocks
    private DefaultTokenService tokenService;

    @Test
    void generateToken() throws Exception {
        ReflectionTestUtils.setField(tokenService, "codeLength", 6);
        ReflectionTestUtils.setField(tokenService, "expirationMinutes", 15);
        UUID fakeUuid = UUID.fromString("00000000-0000-0000-0000-000000000001");
        try (MockedStatic<UUID> mockedStatic = mockStatic(UUID.class)) {
            mockedStatic.when(UUID::randomUUID).thenReturn(fakeUuid);
            when(passwordEncoder.encode(CODE)).thenReturn("password");
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            String key = "%s%s%s".formatted(EMAIL, DELIMITER,
                    MessageType.EMAIL_VERIFICATION.name());

            tokenService.generateToken(EMAIL,
                    MessageType.EMAIL_VERIFICATION);

            verify(valueOperations).set(key, "password", Duration.ofMinutes(15));
            verify(encryptionUtils).encrypt("%s%s%s".formatted(EMAIL, DELIMITER, CODE));
        }
    }

    @Test
    void verifyToken() throws Exception {
        when(encryptionUtils.decrypt("token"))
                .thenReturn("%s:code".formatted(EMAIL));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("%s%s%s".formatted(EMAIL, DELIMITER,
                MessageType.PASSWORD_RESET.name()))).thenReturn(CODE_DECRYPT);
        when(passwordEncoder.matches(CODE_DECRYPT, CODE_DECRYPT)).thenReturn(true);

        assertTrue(tokenService.verifyToken("token",
                EMAIL, MessageType.PASSWORD_RESET));

    }
}
