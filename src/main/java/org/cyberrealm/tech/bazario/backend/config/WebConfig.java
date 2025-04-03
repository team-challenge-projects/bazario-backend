package org.cyberrealm.tech.bazario.backend.config;

import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.interceptors.GlobalRateLimitInterceptor;
import org.cyberrealm.tech.bazario.backend.interceptors.LimitedSecurityRateLimitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final GlobalRateLimitInterceptor globalInterceptor;
    private final LimitedSecurityRateLimitInterceptor limitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(globalInterceptor).addPathPatterns("/**");
        registry.addInterceptor(limitInterceptor).addPathPatterns(
                "/public/login", "/public/refreshToken",
                "/public/registration", "/public/send/**",
                "/public/email/verify", "/public/resetPassword");
    }
}
