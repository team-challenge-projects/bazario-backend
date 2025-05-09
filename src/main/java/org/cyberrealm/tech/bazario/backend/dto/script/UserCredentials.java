package org.cyberrealm.tech.bazario.backend.dto.script;

import lombok.Getter;
import lombok.Setter;
import org.cyberrealm.tech.bazario.backend.model.enums.Role;

@Getter
@Setter
public class UserCredentials {
    private String firstName;
    private String lastName;
    private String avatar;
    private String email;
    private String phoneNumber;
    private String password;
    private String cityName;
    private String cityCoordinate;
    private Role role;
}
