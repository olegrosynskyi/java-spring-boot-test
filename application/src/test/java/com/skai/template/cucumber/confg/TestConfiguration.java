package com.skai.template.cucumber.confg;

import com.kenshoo.auth.JWTTokenHelper;
import com.kenshoo.auth.KenshooPrincipal;
import com.kenshoo.auth.UserRoleUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;

@Configuration
public class TestConfiguration {

    private static final KenshooPrincipal KENSHOO_PRINCIPAL = KenshooPrincipal.newBuilder("cucumber.tests@skai.io")
            .withName("Cucumber Tests")
            .withRoles(List.of(UserRoleUtils.KENSHOO_ADMIN_ROLE))
            .build();

    @Bean
    public RestTemplate restTemplate(@Value("${spring.security.jwt.secret}") String secret) {
        return new RestTemplateBuilder()
                .defaultHeader("Authorization", "Bearer " + getToken(secret))
                .build();
    }

    public String getToken(String secret) {
        try {
            return JWTTokenHelper.generateJsonWebToken(
                    KENSHOO_PRINCIPAL,
                    secret,
                    Duration.ofDays(1).toMillis());
        } catch (Exception e) {
            return null;
        }
    }
}
