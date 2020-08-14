package com.allanweber.candidatescareer.api;

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

    @GetMapping
    public ResponseEntity<?> get() {
        return ok().build();
    }
}
