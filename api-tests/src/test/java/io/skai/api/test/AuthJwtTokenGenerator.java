package io.skai.api.test;

import com.kenshoo.auth.JWTTokenHelper;
import com.kenshoo.auth.KenshooPrincipal;
import com.kenshoo.auth.UserRoleUtils;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class AuthJwtTokenGenerator {

    private static final String TEST_HMAC_SECRET_FROM_APP_PROPERTIES = "sKn25yqQLZmPTEMP";
    private static final String HMAC_SECRET_ENVIRONMENT_KEY = "MICROCOSM_HMAC_SECRET";
    private final KenshooPrincipal kenshooPrincipal = KenshooPrincipal.newBuilder("api.tests@skai.io")
            .withAgencyId(2L)
            .withBillingId(3L)
            .withName("Api Tests")
            .withRoles(List.of(UserRoleUtils.KENSHOO_ADMIN_ROLE))
            .build();


    public String getToken() {
        return getToken(kenshooPrincipal, Duration.ofDays(1));
    }

    public String getToken(KenshooPrincipal kenshooPrincipal, Duration duration) {
        try {
            return JWTTokenHelper.generateJsonWebToken(
                    kenshooPrincipal,
                    getHmacSecret(),
                    duration.toMillis());
        } catch (Exception e) {
            return null;
        }
    }

    private String getHmacSecret() {
        return Optional.ofNullable(System.getProperty(HMAC_SECRET_ENVIRONMENT_KEY))
                .orElse(TEST_HMAC_SECRET_FROM_APP_PROPERTIES);
    }
}
