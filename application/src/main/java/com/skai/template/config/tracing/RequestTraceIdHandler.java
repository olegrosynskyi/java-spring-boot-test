package com.skai.template.config.tracing;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
public class RequestTraceIdHandler implements AsyncHandlerInterceptor {

    private static final String TRACE_ID = "kenshooTraceId";
    private static final String TRACE_ID_HEADER = "X-kenshoo-trace-id";

    private ThreadLocal<Set<String>> storedKeys = ThreadLocal.withInitial(HashSet::new);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String traceID = getTraceID(request);
        addKey(TRACE_ID, traceID);
        response.setHeader(TRACE_ID_HEADER, traceID);
        return true;
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) {
        removeKeys();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        removeKeys();
    }

   private void addKey(String key, String value) {
        MDC.put(key, value);
        storedKeys.get().add(key);
    }

    private String getTraceID(HttpServletRequest request) {
        return Optional
                .ofNullable(request.getHeader(TRACE_ID_HEADER))
                .orElse(UUID.randomUUID().toString());
    }

    private void removeKeys() {
        storedKeys.get().forEach(MDC::remove);
        storedKeys.remove();
    }
}
