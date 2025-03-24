package org.cyberrealm.tech.bazario.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class BazarioBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BazarioBackendApplication.class, args);
    }

}
