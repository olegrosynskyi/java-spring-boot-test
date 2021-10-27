package io.skai.template.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class Http401ForbiddenEntryPointTest {

    @InjectMocks
    private Http401ForbiddenEntryPoint entryPoint;
    @Mock
    private ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<Map<String, Object>> captor;

    @Test
    @SneakyThrows
    public void responseContainsCorrectDetails() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        given(response.getStatus()).willReturn(HttpServletResponse.SC_UNAUTHORIZED);
        given(request.getServletPath()).willReturn("/api/v1/test");
        given(response.getWriter()).willReturn(mock(PrintWriter.class));

        entryPoint.commence(request, response, null);

        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(objectMapper).writeValueAsString(captor.capture());
        Map<String, Object> responseDetails = captor.getValue();
        assertThat(responseDetails.get("status"), equalTo(HttpServletResponse.SC_UNAUTHORIZED));
        assertThat(responseDetails.get("path"), equalTo("/api/v1/test"));
        assertThat(responseDetails.get("message"), equalTo("Access Denied. Credentials are required to access this resource."));
    }
}
