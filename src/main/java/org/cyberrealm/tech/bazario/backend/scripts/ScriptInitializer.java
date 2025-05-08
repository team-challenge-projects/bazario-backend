package org.cyberrealm.tech.bazario.backend.scripts;

import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.config.ScriptCredentials;
import org.cyberrealm.tech.bazario.backend.scripts.service.AdInitializer;
import org.cyberrealm.tech.bazario.backend.scripts.service.AdTypeInitializer;
import org.cyberrealm.tech.bazario.backend.scripts.service.CategoryInitializer;
import org.cyberrealm.tech.bazario.backend.scripts.service.UserInitializer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScriptInitializer implements CommandLineRunner {
    private final ScriptCredentials credentials;
    private final UserInitializer userInitializer;
    private final AdTypeInitializer adTypeInitializer;
    private final CategoryInitializer categoryInitializer;
    private final AdInitializer adInitializer;

    @Override
    public void run(String... args) throws Exception {
        if (credentials == null) {
            return;
        }
        var users = credentials.getUsers().stream().map(userInitializer::getUser).toList();
        var adTypes = credentials.getAdTypeParameters().stream()
                .map(adTypeInitializer::getAdType).toList();
        var categories = credentials.getCategories().stream().map(category ->
                categoryInitializer.getCategory(category, adTypes)).toList();
        credentials.getAds().forEach(ad -> adInitializer.createAd(ad, users,categories, adTypes));

    }
}
