package io.skai.template.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequiredArgsConstructor
public class HomePageController {

    private final BuildProperties buildProperties;

    @GetMapping("/")
    public String welcomePage() {
        return "welcome";
    }

    @ModelAttribute("version")
    public String getApplicationVersion() {
        return buildProperties.getVersion();
    }
}
