package io.skai.template.security;

import com.kenshoo.auth.JWTTokenHelper;
import com.kenshoo.auth.KenshooPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class JWTAuthenticationProvider implements AuthenticationProvider {

    private static final String FAILED_AUTH_ERROR_MESSAGE = "Authentication failed, given token is not a JWT token";

    @Value("${spring.security.jwt.secret}")
    private final String jwtSecret;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String jwtToken = (String) authentication.getCredentials();
        if (JWTTokenHelper.isJwtToken(jwtToken)) {
            KenshooPrincipal authenticatedUser = JWTTokenHelper.parseJwtToken(jwtToken, jwtSecret);
            return Optional.ofNullable(authenticatedUser)
                    .map(UserTokenAuthentication::new)
                    .orElseThrow(() -> new BadCredentialsException(FAILED_AUTH_ERROR_MESSAGE));
        }
        throw new BadCredentialsException(FAILED_AUTH_ERROR_MESSAGE);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JWTAuthenticationToken.class == authentication;
    }
}
