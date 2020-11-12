package com.allanweber.candidates_career_recruiter.authentication.auth.api;

import com.allanweber.candidates_career_recruiter.authentication.auth.dto.AuthUser;
import com.allanweber.candidates_career_recruiter.authentication.auth.dto.LoginRequest;
import com.allanweber.candidates_career_recruiter.authentication.auth.dto.LoginResponse;
import com.allanweber.candidates_career_recruiter.authentication.auth.jwt.TokenDto;
import com.allanweber.candidates_career_recruiter.authentication.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;

    @Override
    public ResponseEntity<LoginResponse> login(@Valid LoginRequest loginRequest) {
        loginRequest.trim();
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @Override
    public ResponseEntity<TokenDto> getRefreshToken(@NotBlank String authHeader) {
        return ResponseEntity.ok(authService.refreshToken(authHeader));
    }

    @Override
    public ResponseEntity<Boolean> admin(@NotBlank String authHeader) {
        return ResponseEntity.ok(authService.isAdmin(authHeader));
    }

    @Override
    public ResponseEntity<AuthUser> authenticated() {
        return ResponseEntity.ok(authService.getAuthUser());
    }
}
