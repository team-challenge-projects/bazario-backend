package org.cyberrealm.tech.bazario.backend.service;

import java.util.List;
import org.cyberrealm.tech.bazario.backend.dto.PageCompareAd;

public interface AdCompareService {
    /**
     * Comparison of ads by price, user rating,
     * distance between users, number of views.
     *
     * @author Andrey Sitarskiy
     * @param ids Ad ids for compare
     * @return Page with dto contains ad fields and compare result.
     */
    PageCompareAd compares(List<Long> ids);
}
