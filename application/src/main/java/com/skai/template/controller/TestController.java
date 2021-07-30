package com.skai.template.controller;

import com.kenshoo.auth.KenshooPrincipal;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.UUID;

import static com.codahale.metrics.MetricRegistry.name;

@RestController
@Slf4j
@RequestMapping(value = "api/v1/test")
@RequiredArgsConstructor
public class TestController {

    private static final String GET_REQUEST_METRIC_NAME = name(TestController.class, "get");

    private final MeterRegistry meterRegistry;

    @Counted
    @GetMapping
    public String get() {
        log.info(Markers.append("userId", UUID.randomUUID().toString()), "Received request to test controller");
        return meterRegistry.timer(GET_REQUEST_METRIC_NAME).record(() -> "You successfully sent a GET request");
    }

    @Timed
    @PostMapping
    public String post(@ApiIgnore Authentication authentication) {
        KenshooPrincipal principal = (KenshooPrincipal) authentication.getPrincipal();
        return "You successfully sent a POST request to a secured endpoint. " +
                "KenshooPrincipal details : email : " + principal.getEmail();
    }

}
