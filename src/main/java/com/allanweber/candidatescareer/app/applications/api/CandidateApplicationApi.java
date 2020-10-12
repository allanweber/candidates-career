package com.allanweber.candidatescareer.app.applications.api;

import com.allanweber.candidatescareer.app.candidate.dto.CandidateProfile;
import com.allanweber.candidatescareer.app.helper.ConstantsUtils;
import com.allanweber.candidatescareer.app.vacancy.dto.VacancyDto;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "Candidates Application Api")
@RequestMapping("/candidate-application")
public interface CandidateApplicationApi {

    String ID = "applicationId";
    String ID_DESCRIPTION = "candidate application Id";
    String NOT_FIND_APPLICATION = "Could not find application";
    String ACCESS_TOKEN = "candidate-application-access-token";

    @ApiOperation(notes = "Accept application and redirect user to Anonymous application Page", value = "Redirect user to Anonymous application Page")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Application accepted"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = NOT_FIND_APPLICATION)})
    @GetMapping("/{applicationId}")
    ResponseEntity<Void> accept(@ApiParam(name = ID, value = ID_DESCRIPTION, required = true) @PathVariable(name = ID) String applicationd);

    @ApiOperation(notes = "Deny application and redirect user to Deny Page", value = "Deny application and redirect user to Deny Page")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Application denied"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = NOT_FIND_APPLICATION)})
    @GetMapping("/{applicationId}/deny")
    ResponseEntity<Void> deny(@ApiParam(name = ID, value = ID_DESCRIPTION, required = true) @PathVariable(name = ID) String applicationId);

    @ApiOperation(notes = "Send application data and complete the application process", value = "Send application data")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Application completed"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = NOT_FIND_APPLICATION)})
    @PostMapping("/{applicationId}")
    ResponseEntity<Void> apply(@RequestHeader(name = ACCESS_TOKEN) String accessToken,
                               @ApiParam(name = ID, value = ID_DESCRIPTION, required = true) @PathVariable(name = ID) String applicationId,
                               @Valid @RequestBody CandidateProfile body);

    @ApiOperation(notes = "Get candidate profile for anonymous application", value = "Get candidate profile for anonymous application", response = CandidateProfile.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Application completed"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = NOT_FIND_APPLICATION),
            @ApiResponse(code = 403, message = "Invalid access token")})
    @GetMapping("/{applicationId}/profile")
    ResponseEntity<CandidateProfile> getProfile(@RequestHeader(name = ACCESS_TOKEN) String accessToken,
                                                @ApiParam(name = ID, value = ID_DESCRIPTION, required = true) @PathVariable(name = ID) String applicationId);

    @ApiOperation(notes = "Validate access token", value = "Validate access token")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Access token is valid"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 403, message = "Invalid access token")})
    @GetMapping("/validate-access/{applicationId}")
    ResponseEntity<Void> validateAccess(@RequestHeader(name = ACCESS_TOKEN) String accessToken,
                                        @ApiParam(name = ID, value = ID_DESCRIPTION, required = true) @PathVariable(name = ID) String applicationId);

    @ApiOperation(notes = "Get the vacancy details for the registry", value = "Get the vacancy details", response = VacancyDto.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Vacancy returned"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = NOT_FIND_APPLICATION)})
    @GetMapping("/{applicationId}/vacancy")
    ResponseEntity<VacancyDto> getVacancy(@RequestHeader(name = ACCESS_TOKEN) String accessToken,
                                        @ApiParam(name = ID, value = ID_DESCRIPTION, required = true) @PathVariable(name = ID) String applicationId);
}
