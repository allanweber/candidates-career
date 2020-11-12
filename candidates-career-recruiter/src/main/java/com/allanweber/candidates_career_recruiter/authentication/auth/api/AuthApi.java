package com.allanweber.candidates_career_recruiter.authentication.auth.api;

import com.allanweber.candidates_career_recruiter.authentication.auth.dto.AuthUser;
import com.allanweber.candidates_career_recruiter.authentication.auth.dto.LoginRequest;
import com.allanweber.candidates_career_recruiter.authentication.auth.dto.LoginResponse;
import com.allanweber.candidates_career_recruiter.authentication.auth.jwt.TokenDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "Authentication Methods")
@RequestMapping("/auth")
public interface AuthApi {

    String AUTHENTICATION_IS_INVALID = "Authentication is invalid";

    @ApiOperation(notes = "Execute user login", value = "Execute user login", response = LoginResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success login"),
            @ApiResponse(code = 400, message = "Login request is invalid"),
            @ApiResponse(code = 401, message = AUTHENTICATION_IS_INVALID)})
    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) ;

    @ApiOperation(notes = "Refresh user token", value = "Refresh user token", response = TokenDto.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Token refreshed"),
            @ApiResponse(code = 401, message = AUTHENTICATION_IS_INVALID)})
    @GetMapping("/refreshToken")
    ResponseEntity<TokenDto> getRefreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader);

    @ApiOperation(notes = "Check if user is Admin role", value = "Check if user is Admin role", response = Boolean.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "User checked"),
            @ApiResponse(code = 401, message = AUTHENTICATION_IS_INVALID)})
    @GetMapping("/is-admin")
    ResponseEntity<Boolean> admin(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader);

    @ApiOperation(notes = "Check if user is authenticated", value = "Check if user is authenticated", response = AuthUser.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Authenticated user"),
            @ApiResponse(code = 401, message = AUTHENTICATION_IS_INVALID)})
    @GetMapping("/auth-user")
    ResponseEntity<AuthUser> authenticated();
}
