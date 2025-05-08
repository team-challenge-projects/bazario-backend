package org.cyberrealm.tech.bazario.backend.service;

import java.util.Map;
import org.springframework.data.domain.Pageable;

public interface PageableService {
    Pageable get(Map<String, String> filter);
}
