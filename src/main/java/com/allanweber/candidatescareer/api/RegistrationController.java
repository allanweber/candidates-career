package com.allanweber.candidatescareer.api;

import com.allanweber.candidatescareer.domain.user.UserService;
import com.allanweber.candidatescareer.domain.user.dto.UserDto;
import com.allanweber.candidatescareer.domain.user.registration.RegistrationService;
import com.allanweber.candidatescareer.domain.user.registration.VerificationService;
import com.allanweber.candidatescareer.domain.user.registration.dto.ChangePasswordRequest;
import com.allanweber.candidatescareer.domain.user.registration.dto.RememberMeRequest;
import com.allanweber.candidatescareer.domain.user.registration.dto.RememberMeResponse;
import com.allanweber.candidatescareer.domain.user.registration.dto.UserRegistration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
public class RegistrationController implements RegistrationApi {

    private final UserService userService;
    private final VerificationService verificationService;
    private final RegistrationService registrationService;

    @Override
    public ResponseEntity<UserDto> signUp(@Valid UserRegistration user) {
        return ok(userService.createUser(user));
    }

    @Override
    public ResponseEntity<Void> verify(String id, String email) {
        String redirection = verificationService.verify(id, email);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirection))
                .build();
    }

    @Override
    public ResponseEntity<RememberMeResponse> rememberMe(@Valid RememberMeRequest rememberMe) {
        RememberMeResponse rememberMeResponse = registrationService.rememberMe(rememberMe);
        ResponseEntity<RememberMeResponse> response;
        if(rememberMeResponse.isOk()) {
            response = ok().build();
        } else {
            response =  badRequest().body(rememberMeResponse);
        }
        return response;
    }

    @Override
    public ResponseEntity<RememberMeResponse> changePassword(@Valid ChangePasswordRequest changePasswordRequest) {
        RememberMeResponse rememberMeResponse = registrationService.changePassword(changePasswordRequest);
        ResponseEntity<RememberMeResponse> response;
        if(rememberMeResponse.isOk()) {
            response = ok().build();
        } else {
            response =  badRequest().body(rememberMeResponse);
        }
        return response;
    }
}
