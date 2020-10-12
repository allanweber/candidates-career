package com.allanweber.candidatescareer.authentication.user.api;

import com.allanweber.candidatescareer.authentication.user.dto.UserDto;
import com.allanweber.candidatescareer.authentication.user.registration.dto.ChangePasswordRequest;
import com.allanweber.candidatescareer.authentication.user.registration.dto.RememberMeRequest;
import com.allanweber.candidatescareer.authentication.user.registration.dto.RememberMeResponse;
import com.allanweber.candidatescareer.authentication.user.registration.dto.UserRegistration;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "Register new users")
@RequestMapping("/registration")
public interface RegistrationApi {

    @ApiOperation(notes = "Create a new uer via signUp", value = "SignUp a user", response = UserDto.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "User created"),
            @ApiResponse(code = 400, message = "User registration data is invalid")})
    @PostMapping("/signUp")
    ResponseEntity<UserDto> signUp(@Valid @RequestBody UserRegistration user);

    @ApiOperation(notes = "Apply the email verification for new user", value = "Email verification")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User verified"),
            @ApiResponse(code = 404, message = "Verification does not exist or is invalid")})
    @GetMapping("/verify/email")
    ResponseEntity<Void> verify(@RequestParam("id") String id, @RequestParam("email") String email);

    @ApiOperation(notes = "Apply user data to change password", value = "Change password", response = RememberMeResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Change password sent"),})
    @PostMapping("/remember-me")
    ResponseEntity<RememberMeResponse> rememberMe(@Valid @RequestBody RememberMeRequest rememberMe);

    @ApiOperation(notes = "Change user password", value = "Change user password", response = RememberMeResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Password changed"),})
    @PostMapping("/change-password")
    ResponseEntity<RememberMeResponse> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest);
}
