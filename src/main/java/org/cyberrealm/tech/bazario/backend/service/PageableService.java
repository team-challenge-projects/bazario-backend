package org.cyberrealm.tech.bazario.backend.service;

import java.util.Map;
import org.springframework.data.domain.Pageable;

public interface PageableService {
    /**
     * Parse filter and get Pageable
     *
     * @author Andrey Sitarskiy
     * @param filter Filter contains filed (size, page, sort).
     *              Name parameter and value.
     * @return Pageable
     */
    Pageable get(Map<String, String> filter);
}
