package io.skai.template.security;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.SAME_THREAD)
public class JWTAuthenticationFilterTest {

    @InjectMocks
    private JWTAuthenticationFilter filter;
    @Mock
    private AuthenticationManager authenticationManager;

    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final FilterChain filterChain = mock(FilterChain.class);

    @Captor
    private ArgumentCaptor<JWTAuthenticationToken> captor;

    @Test
    @SneakyThrows
    public void continueFilterChainInCaseNoTokenProvided() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(authenticationManager, never()).authenticate(any(Authentication.class));
        assertThat(SecurityContextHolder.getContext().getAuthentication(), nullValue());
    }

    @Test
    @SneakyThrows
    public void continueFilterChainIfTokenPrefixNotSupported() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn("not supported token");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(authenticationManager, never()).authenticate(any(Authentication.class));
        assertThat(SecurityContextHolder.getContext().getAuthentication(), nullValue());
    }

    @Test
    @SneakyThrows
    public void stopFilterChainInCaseOfError() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn(JWTAuthenticationFilter.PREFIX + " test token");
        given(authenticationManager.authenticate(any(JWTAuthenticationToken.class))).willThrow(new BadCredentialsException("Provided credentials are not valid"));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication(), nullValue());
    }

    @Test
    @SneakyThrows
    public void authenticateInCaseOfValidToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Authentication authResult = mock(Authentication.class);
        given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn(JWTAuthenticationFilter.PREFIX + "token");
        given(authenticationManager.authenticate(captor.capture())).willReturn(authResult);

        filter.doFilterInternal(request, response, filterChain);

        JWTAuthenticationToken jwtAuthenticationToken = captor.getValue();
        assertThat(jwtAuthenticationToken.getCredentials(), equalTo("token"));
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication(), equalTo(authResult));
    }
}
