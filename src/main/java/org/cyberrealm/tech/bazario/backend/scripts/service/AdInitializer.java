package org.cyberrealm.tech.bazario.backend.scripts.service;

import java.util.List;
import org.cyberrealm.tech.bazario.backend.dto.script.AdCredentials;
import org.cyberrealm.tech.bazario.backend.model.Category;
import org.cyberrealm.tech.bazario.backend.model.TypeAdParameter;
import org.cyberrealm.tech.bazario.backend.model.User;

public interface AdInitializer {
    void createAd(AdCredentials ad, List<User> users, List<Category> categories,
                  List<TypeAdParameter> adTypes);

    void createAds(List<AdCredentials> credentials, List<User> users,
                   List<Category> categories, List<TypeAdParameter> adType);
}
