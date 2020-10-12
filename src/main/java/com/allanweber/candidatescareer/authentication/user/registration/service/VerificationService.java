package com.allanweber.candidatescareer.authentication.user.registration.service;

import com.allanweber.candidatescareer.authentication.user.registration.repository.Verification;
import com.allanweber.candidatescareer.authentication.user.registration.repository.VerificationRepository;
import com.allanweber.candidatescareer.authentication.user.service.UserService;
import com.allanweber.candidatescareer.infrastructure.configuration.AppHostConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private static final String AUTH_PATH = "auth";
    private static final String EMAIL_VERIFIED_PATH = "email-verified";

    private final VerificationRepository verificationRepository;
    private final UserService userService;
    private final AppHostConfiguration appHostConfiguration;

    public Verification createVerification(String email) {
        if (!verificationRepository.existsByEmail(email)) {
            Verification verification = new Verification(email);
            verificationRepository.save(verification);
        }
        return getVerificationIdByEmail(email);
    }

    public String verify(String id, String email) {
        String userEmail = getVerification(id, email);
        userService.setUserVerified(userEmail);
        remove(id);
        return UriComponentsBuilder.newInstance()
                .uri(URI.create(appHostConfiguration.getFrontEnd()))
                .pathSegment(AUTH_PATH)
                .pathSegment(EMAIL_VERIFIED_PATH)
                .toUriString();
    }

    private Verification getVerificationIdByEmail(String email) {
        return verificationRepository.findByEmail(email)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, "Invalid verification"));
    }

    private String getVerification(String id, String email) {
        Optional<Verification> verification = verificationRepository.findByIdAndEmail(id, email);
        return verification.map(Verification::getEmail)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, "Invalid verification"));
    }

    private void remove(String id) {
        Verification verification = verificationRepository.findById(id)
                .orElseThrow(() -> new HttpClientErrorException(BAD_REQUEST, "Invalid verification id"));
        verificationRepository.delete(verification);
    }
}
