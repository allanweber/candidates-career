package com.allanweber.candidatescareer.api;

import com.allanweber.candidatescareer.domain.user.dto.UserDto;
import com.allanweber.candidatescareer.domain.user.registration.dto.UserRegistration;
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
}
