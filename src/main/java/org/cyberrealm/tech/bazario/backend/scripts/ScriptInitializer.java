package org.cyberrealm.tech.bazario.backend.scripts;

import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.config.ScriptCredentials;
import org.cyberrealm.tech.bazario.backend.scripts.service.AdInitializer;
import org.cyberrealm.tech.bazario.backend.scripts.service.AdParameterInitializer;
import org.cyberrealm.tech.bazario.backend.scripts.service.AdTypeInitializer;
import org.cyberrealm.tech.bazario.backend.scripts.service.CategoryInitializer;
import org.cyberrealm.tech.bazario.backend.scripts.service.UserInitializer;
import org.cyberrealm.tech.bazario.backend.scripts.service.UserParameterInitializer;
import org.cyberrealm.tech.bazario.backend.scripts.service.UserTypeInitializer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * A script for adding default users and their parameters,
 * ads for these users and their parameters, categories
 * and related parameters.
 */
@Component
@RequiredArgsConstructor
public class ScriptInitializer implements CommandLineRunner {
    private final ScriptCredentials credentials;
    private final UserInitializer userInitializer;
    private final UserTypeInitializer userTypeInitializer;
    private final UserParameterInitializer userParameterInitializer;
    private final AdTypeInitializer adTypeInitializer;
    private final CategoryInitializer categoryInitializer;
    private final AdInitializer adInitializer;
    private final AdParameterInitializer adParameterInitializer;

    @Override
    public void run(String... args) throws Exception {
        if (credentials == null) {
            return;
        }
        var users = userInitializer.getUsers(credentials.getUsers());
        var userTypeParameters = userTypeInitializer.getUserType(
                credentials.getUserTypeParameters());
        userParameterInitializer.addParameters(credentials.getUserParameters(),
                users, userTypeParameters);
        var adTypes = adTypeInitializer.getAdTypes(credentials.getAdTypeParameters());
        var categories = categoryInitializer.getCategories(credentials.getCategories(),
                adTypes);
        var ads = adInitializer.createAds(credentials.getAds(), users, categories);
        adParameterInitializer.addParameters(credentials.getAdParameters(), ads, adTypes);

    }
}
