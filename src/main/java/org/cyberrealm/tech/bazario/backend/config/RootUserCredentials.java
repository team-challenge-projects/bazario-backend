package org.cyberrealm.tech.bazario.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "user.root.credentials")
@Getter
@Setter
public class RootUserCredentials {
    private String firstName;
    private String lastName;
    private String avatar;
    private String email;
    private String phoneNumber;
    private String password;
    private String cityName;
    private String cityCoordinate;
}
