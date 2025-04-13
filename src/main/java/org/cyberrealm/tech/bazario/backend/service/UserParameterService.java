package org.cyberrealm.tech.bazario.backend.service;

import java.util.List;
import java.util.Map;

public interface UserParameterService {
    List<Long> filterByParam(Map<Long, String> filters);
}
