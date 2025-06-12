package org.cyberrealm.tech.bazario.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.cyberrealm.tech.bazario.backend.scripts.ScriptInitializer;
import org.cyberrealm.tech.bazario.backend.security.RootUserInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class AbstractIntegrationTest {
    protected static final long ID_ONE = 1L;
    protected static final long ID_TWO = 2L;

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
}
