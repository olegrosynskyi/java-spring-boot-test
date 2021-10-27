package io.skai.api.test;

import com.kenshoo.auth.JWTTokenHelper;
import com.kenshoo.auth.KenshooPrincipal;
import com.kenshoo.auth.UserRoleUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class AuthJwtTokenGenerator {

    private static final String TEST_HMAC_SECRET_FROM_APP_PROPERTIES = "sKn25yqQLZmPTEMP";
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
                    TEST_HMAC_SECRET_FROM_APP_PROPERTIES,
                    duration.toMillis());
        }catch (Exception e) {
            return null;
        }
    }

    private Date getTokenExpirationDate() {
        return new Date(LocalDateTime.now()
                .plusDays(1)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli());
    }

}
