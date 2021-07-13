package com.skai.template.controller;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.codahale.metrics.MetricRegistry.name;

@RestController
@RequestMapping(value = "api/v1/test")
@RequiredArgsConstructor
public class TestController {

    private static final String GET_REQUEST_METRIC_NAME = name(TestController.class, "get");

    private final MeterRegistry meterRegistry;

    @Counted
    @GetMapping
    public String get() {
        return meterRegistry.timer(GET_REQUEST_METRIC_NAME).record(() -> "You successfully sent a GET request");
    }

    @Timed
    @PostMapping
    public String post() {
        return "You successfully sent a POST request";
    }

}
