package org.cyberrealm.tech.bazario.backend.service;

import java.util.List;

public interface AdParameterService {
    /**
     * Filters parameters by the fields specified in the filter
     * and returns a list of ad IDs associated with them.
     *
     * @author Andrey Sitarskiy
     * @param ids category type ids
     * @return List of ad IDs associated with ad parameters
     */
    List<Long> filterByParam(String ids);
}
