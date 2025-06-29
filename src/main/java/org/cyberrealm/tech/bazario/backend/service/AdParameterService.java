package org.cyberrealm.tech.bazario.backend.service;

import java.util.List;
import java.util.Map;

public interface AdParameterService {
    /**
     * Filters parameters by the fields specified in the filter
     * and returns a list of ad IDs associated with them.
     *
     * @author Andrey Sitarskiy
     * @param filters Name field and value
     * @return List of ad IDs associated with ad parameters
     */
    List<Long> filterByParam(Map<Long, String> filters);
}
