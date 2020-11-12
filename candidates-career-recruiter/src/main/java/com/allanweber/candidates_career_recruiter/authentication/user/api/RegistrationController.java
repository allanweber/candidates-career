package com.allanweber.candidates_career_recruiter.authentication.user.api;

import com.allanweber.candidates_career_recruiter.authentication.user.dto.UserDto;
import com.allanweber.candidates_career_recruiter.authentication.user.registration.dto.ChangePasswordRequest;
import com.allanweber.candidates_career_recruiter.authentication.user.registration.dto.RememberMeRequest;
import com.allanweber.candidates_career_recruiter.authentication.user.registration.dto.RememberMeResponse;
import com.allanweber.candidates_career_recruiter.authentication.user.registration.dto.UserRegistration;
import com.allanweber.candidates_career_recruiter.authentication.user.registration.service.RegistrationService;
import com.allanweber.candidates_career_recruiter.authentication.user.registration.service.VerificationService;
import com.allanweber.candidates_career_recruiter.authentication.user.service.UserService;
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
    public ResponseEntity<UserDto> signUp(@Valid UserRegistration userRegistration) {
        userRegistration.trim();
        return ok(userService.createUser(userRegistration));
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
        rememberMe.trim();
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
        changePasswordRequest.trim();
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
