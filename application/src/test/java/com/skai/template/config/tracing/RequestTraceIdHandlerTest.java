package com.skai.template.config.tracing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestTraceIdHandlerTest {

    private static final String TRACE_ID = "kenshooTraceId";
    private static final String TRACE_ID_HEADER = "X-kenshoo-trace-id";

    private static final String EXISTING_TRACE_ID = "some-trace-id";
    private static final String SOME_MDC_VALUE = "some-value";
    private static final String SOME_MDC_KEY = "some-key";

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;

    @InjectMocks
    private RequestTraceIdHandler requestTraceIdHandler;

    @Test
    void willNotRemoveKeysIfDidNotSet() {
        MDC.put(SOME_MDC_KEY, SOME_MDC_VALUE);
        when(request.getHeader(TRACE_ID_HEADER)).thenReturn(EXISTING_TRACE_ID);

        requestTraceIdHandler.preHandle(request, response, session);
        requestTraceIdHandler.afterCompletion(request, response, null, null);

        assertThat(MDC.get(TRACE_ID), is(nullValue()));
        assertThat(MDC.get(SOME_MDC_KEY), is(SOME_MDC_VALUE));
    }

    @Test
    void removeMDCTraceIdAtRequestCompletion() {
        when(request.getHeader(TRACE_ID_HEADER)).thenReturn(EXISTING_TRACE_ID);

        requestTraceIdHandler.preHandle(request, response, session);
        requestTraceIdHandler.afterCompletion(request, response, null, null);

        assertThat(MDC.get(TRACE_ID), is(nullValue()));
    }


    @Test
    void removeMDCTraceIdAtConcurrentCompletion() {
        when(request.getHeader(TRACE_ID_HEADER)).thenReturn(EXISTING_TRACE_ID);

        requestTraceIdHandler.preHandle(request, response, session);
        requestTraceIdHandler.afterConcurrentHandlingStarted(request, response, requestTraceIdHandler);

        assertThat(MDC.get(TRACE_ID), is(nullValue()));
    }

    @Test
    void addExistingTraceIdToMDC() {
        when(request.getHeader(TRACE_ID_HEADER)).thenReturn(EXISTING_TRACE_ID);

        requestTraceIdHandler.preHandle(request, response, session);

        assertThat(MDC.get(TRACE_ID), is(EXISTING_TRACE_ID));
    }

    @Test
    void createTraceIdWhenNoExistingId() {
        when(request.getHeader(TRACE_ID_HEADER)).thenReturn(null);

        requestTraceIdHandler.preHandle(request, response, session);

        assertThat(MDC.get(TRACE_ID), is(notNullValue()));
    }
}