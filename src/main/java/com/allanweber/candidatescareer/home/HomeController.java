package com.allanweber.candidatescareer.home;

import com.allanweber.candidatescareer.configuration.AppConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final AppConfiguration appConfiguration;

    @GetMapping
    public ResponseEntity<?> get() {
        return ok(appConfiguration.getSomeProperty());
    }
}
