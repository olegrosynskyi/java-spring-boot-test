package com.skai.template.security;

import com.kenshoo.auth.KenshooPrincipal;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserTokenAuthentication extends AbstractAuthenticationToken {

    private final KenshooPrincipal kenshooPrincipal;

    public UserTokenAuthentication(KenshooPrincipal kenshooPrincipal) {
        super(convertRolesToGrantedAuthorities(kenshooPrincipal.getRoles()));
        this.kenshooPrincipal = kenshooPrincipal;
        super.setAuthenticated(true);
    }

    private static Collection<? extends GrantedAuthority> convertRolesToGrantedAuthorities(List<String> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return List.of();
        }
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.kenshooPrincipal;
    }
}
