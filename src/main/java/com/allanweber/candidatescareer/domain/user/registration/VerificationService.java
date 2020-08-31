package com.allanweber.candidatescareer.domain.user.registration;

import com.allanweber.candidatescareer.domain.user.UserService;
import com.allanweber.candidatescareer.domain.user.registration.repository.Verification;
import com.allanweber.candidatescareer.domain.user.registration.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationRepository verificationRepository;
    private final UserService userService;

    public Verification createVerification(String email) {
        if (!verificationRepository.existsByEmail(email)) {
            Verification verification = new Verification(email);
            verificationRepository.save(verification);
        }
        return getVerificationIdByEmail(email);
    }

    public void verify(String id, String email) {
        String userEmail = getVerification(id, email);
        userService.setUserVerified(userEmail);
        remove(id);
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
