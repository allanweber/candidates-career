package com.allanweber.candidatescareer.api;

import com.allanweber.candidatescareer.domain.candidate.CandidateRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
public class CandidateRegisterController implements CandidateRegisterApi {

    private final CandidateRegisterService candidateRegisterService;

    @Override
    public ResponseEntity<Void> accept(String registerId) {
        String redirection = candidateRegisterService.accept(registerId);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirection))
                .build();
    }

    @Override
    public ResponseEntity<Void> deny(String registerId) {
        String redirection = candidateRegisterService.deny(registerId);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirection))
                .build();
    }

    @Override
    public ResponseEntity<?> register(String registerId) {
        return ok(candidateRegisterService.register(registerId));
    }
}
