package org.cyberrealm.tech.bazario.backend.config;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.cyberrealm.tech.bazario.backend.dto.script.AdCredentials;
import org.cyberrealm.tech.bazario.backend.dto.script.BasicTypeParameter;
import org.cyberrealm.tech.bazario.backend.dto.script.CategoryCredentials;
import org.cyberrealm.tech.bazario.backend.dto.script.UserCredentials;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "script.credentials")
@Getter
@Setter
public class ScriptCredentials {
    private List<UserCredentials> users;
    private List<BasicTypeParameter> adTypeParameters;
    private List<CategoryCredentials> categories;
    private List<AdCredentials> ads;
}
