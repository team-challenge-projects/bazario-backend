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
        var users = userInitializer.getUsers(credentials.getUsers());
        var adTypes = adTypeInitializer.getAdTypes(credentials.getAdTypeParameters());
        var categories = categoryInitializer.getCategories(credentials.getCategories(),adTypes);
        adInitializer.createAds(credentials.getAds(), users, categories, adTypes);

    }
}
