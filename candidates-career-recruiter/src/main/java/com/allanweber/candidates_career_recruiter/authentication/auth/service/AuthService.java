package com.allanweber.candidates_career_recruiter.authentication.auth.service;

import com.allanweber.candidates_career_recruiter.authentication.auth.dto.AuthUser;
import com.allanweber.candidates_career_recruiter.authentication.auth.dto.LoginRequest;
import com.allanweber.candidates_career_recruiter.authentication.auth.dto.LoginResponse;
import com.allanweber.candidates_career_recruiter.authentication.auth.jwt.JwtUtils;
import com.allanweber.candidates_career_recruiter.authentication.auth.jwt.TokenDto;
import com.allanweber.candidates_career_recruiter.authentication.user.dto.AuthoritiesHelper;
import com.allanweber.candidates_career_recruiter.authentication.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    public LoginResponse login(@Valid LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        var user = (User) authentication.getPrincipal();

        var roles = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        var tokenDto = jwtUtils.generateJwtToken(user.getUsername(), roles);

        AuthUser authUser = getAuthUser(user.getUsername());

        return new LoginResponse("bearer", tokenDto.getToken(), roles, tokenDto.getExpirationIn(), tokenDto.getIssuedAt(), authUser);
    }

    public TokenDto refreshToken(@NotBlank String authHeader) {
        return jwtUtils.refreshToken(authHeader);
    }

    public Boolean isAdmin(String authHeader) {
        return jwtUtils.getRoles(authHeader).contains(AuthoritiesHelper.ROLE_ADMIN);
    }

    public AuthUser getAuthUser() {
        var user = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return getAuthUser(user);
    }

    public String getUserName() {
        return Optional.ofNullable(getAuthUser())
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Not authenticated to get current user"))
                .getUserName();
    }

    private AuthUser getAuthUser(String userName) {
        var userDto = userService.getByUserName(userName);
        return new AuthUser(userName, Optional.ofNullable(userDto.getFirstName()).orElse(userDto.getUserName()));
    }
}
