package io.skai.template.config;

import com.kenshoo.auth.UserRoleUtils;
import io.skai.template.security.Http401ForbiddenEntryPoint;
import io.skai.template.security.JWTAuthenticationFilter;
import io.skai.template.security.JWTAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JWTAuthenticationProvider jwtAuthenticationProvider;
    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final Http401ForbiddenEntryPoint entryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf().disable()
                .addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class)
                .authenticationProvider(jwtAuthenticationProvider)
                .authorizeRequests()
                .requestMatchers(EndpointRequest.to(HealthEndpoint.class)).permitAll()
                .antMatchers("/actuator/**").hasAuthority(UserRoleUtils.KENSHOO_ADMIN_ROLE)
                .antMatchers("/", "/v3/api-docs/**", "/css/**", "/images/**",
                        "/webjars/**", "/favicon.ico", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().disable()
                .logout().disable()
                .exceptionHandling().authenticationEntryPoint(entryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .build();
    }
}
