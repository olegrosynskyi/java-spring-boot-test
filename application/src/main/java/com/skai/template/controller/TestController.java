package com.skai.template.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1/test")
public class TestController {

    @GetMapping
    public String get() {
        return "You successfully sent a GET request";
    }

    @PostMapping
    public String post() {
        return "You successfully sent a POST request";
    }

}
