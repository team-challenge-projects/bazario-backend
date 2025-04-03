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
        registry.addInterceptor(globalInterceptor).addPathPatterns("/api/**");
        registry.addInterceptor(limitInterceptor).addPathPatterns(
                "/api/public/login", "/api/public/refreshToken",
                "/api/public/registration", "api/public/send/**",
                "/api/public/email/verify", "/api/public/resetPassword");
    }
}
