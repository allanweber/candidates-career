package com.allanweber.candidatescareer.api;

import com.allanweber.candidatescareer.domain.candidate.dto.CandidateProfile;
import com.allanweber.candidatescareer.domain.vacancy.dto.VacancyDto;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "Candidates Register Api")
@RequestMapping("/candidate-register")
public interface CandidateRegisterApi {

    String ID = "registerId";
    String ID_DESCRIPTION = "candidate register Id";
    String REGISTER_NOT_FOUND = "Could not find register";
    String ACCESS_TOKEN = "candidate-register-access-token";

    @ApiOperation(notes = "Accept register and redirect user to Anonymous Register Page", value = "Redirect user to Anonymous Register Page")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Register accepted"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = REGISTER_NOT_FOUND)})
    @GetMapping("/{registerId}")
    ResponseEntity<Void> accept(@ApiParam(name = ID, value = ID_DESCRIPTION, required = true) @PathVariable(name = ID) String registerId);

    @ApiOperation(notes = "Deny register and redirect user to Deny Page", value = "Deny register and redirect user to Deny Page")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Register denied"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = REGISTER_NOT_FOUND)})
    @GetMapping("/{registerId}/deny")
    ResponseEntity<Void> deny(@ApiParam(name = ID, value = ID_DESCRIPTION, required = true) @PathVariable(name = ID) String registerId);

    @ApiOperation(notes = "Send register data and complete the register process", value = "Send register data")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Register completed"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = REGISTER_NOT_FOUND)})
    @PostMapping("/{registerId}")
    ResponseEntity<Void> register(@RequestHeader(name = ACCESS_TOKEN) String accessToken,
            @ApiParam(name = ID, value = ID_DESCRIPTION, required = true) @PathVariable(name = ID) String registerId,
                               @Valid @RequestBody CandidateProfile body);

    @ApiOperation(notes = "Get candidate profile for anonymous register", value = "Get candidate profile for anonymous register", response = CandidateProfile.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Register completed"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = REGISTER_NOT_FOUND),
            @ApiResponse(code = 403, message = "Invalid access token")})
    @GetMapping("/{registerId}/profile")
    ResponseEntity<CandidateProfile> getProfile(@RequestHeader(name = ACCESS_TOKEN) String accessToken,
                                                @ApiParam(name = ID, value = ID_DESCRIPTION, required = true) @PathVariable(name = ID) String registerId);

    @ApiOperation(notes = "Validate access token", value = "Validate access token")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Access token is valid"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 403, message = "Invalid access token")})
    @GetMapping("/validate-access/{registerId}")
    ResponseEntity<Void> validateAccess(@RequestHeader(name = ACCESS_TOKEN) String accessToken,
                                        @ApiParam(name = ID, value = ID_DESCRIPTION, required = true) @PathVariable(name = ID) String registerId);

    @ApiOperation(notes = "Get the vacancy details for the registry", value = "Get the vacancy details", response = VacancyDto.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Vacancy returned"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = REGISTER_NOT_FOUND)})
    @GetMapping("/{registerId}/vacancy")
    ResponseEntity<VacancyDto> getVacancy(@RequestHeader(name = ACCESS_TOKEN) String accessToken,
                                        @ApiParam(name = ID, value = ID_DESCRIPTION, required = true) @PathVariable(name = ID) String registerId);
}
