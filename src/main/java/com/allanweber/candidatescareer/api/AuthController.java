package com.allanweber.candidatescareer.api;

import com.allanweber.candidatescareer.domain.auth.AuthService;
import com.allanweber.candidatescareer.domain.auth.dto.AuthUser;
import com.allanweber.candidatescareer.domain.auth.dto.LoginRequest;
import com.allanweber.candidatescareer.domain.auth.dto.LoginResponse;
import com.allanweber.candidatescareer.domain.auth.jwt.TokenDto;
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
