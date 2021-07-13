package com.skai.template.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTAuthenticationFilter extends OncePerRequestFilter {

    public final static String PREFIX = "Bearer ";
    private final AuthenticationManager authenticationManager;
    private final AuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();
    private final AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isEmpty(header) || !header.startsWith(PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            String token = header.replace(PREFIX, "");
            JWTAuthenticationToken authRequest = new JWTAuthenticationToken(token);
            authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
            Authentication authResult = this.authenticationManager.authenticate(authRequest);
            SecurityContextHolder.getContext().setAuthentication(authResult);
        } catch (AuthenticationException failed) {
            SecurityContextHolder.clearContext();
            failureHandler.onAuthenticationFailure(request, response, failed);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
