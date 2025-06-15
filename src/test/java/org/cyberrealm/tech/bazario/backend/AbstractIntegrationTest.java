package org.cyberrealm.tech.bazario.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.cyberrealm.tech.bazario.backend.scripts.ScriptInitializer;
import org.cyberrealm.tech.bazario.backend.security.RootUserInitializer;
import org.cyberrealm.tech.bazario.backend.service.EmailSender;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public abstract class AbstractIntegrationTest {
    protected static final long ID_ONE = 1L;
    protected static final long ID_TWO = 2L;
    protected static final long ID_THREE = 3L;

    @Autowired
    protected MockMvc mockMvc;
    @MockitoBean
    protected ScriptInitializer scriptInitializer;
    @MockitoBean
    protected RootUserInitializer rootUserInitializer;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected EntityManager entityManager;
    @MockitoBean
    protected RedisTemplate<String, Object> redisTemplate;
    @MockitoBean
    protected EmailSender emailSender;
}
