package com.allanweber.candidatescareer.api;

import com.allanweber.candidatescareer.domain.user.UserService;
import com.allanweber.candidatescareer.domain.user.dto.UserDto;
import com.allanweber.candidatescareer.domain.user.registration.VerificationService;
import com.allanweber.candidatescareer.domain.user.registration.dto.UserRegistration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
public class RegistrationController implements RegistrationApi {

    private final UserService userService;
    private final VerificationService verificationService;

    @Override
    public ResponseEntity<UserDto> signUp(@Valid UserRegistration user) {
        return ok(userService.createUser(user));
    }

    @Override
    public ResponseEntity<Void> verify(String id, String email) {
        verificationService.verify(id, email);
        return ok().build();
    }
}
