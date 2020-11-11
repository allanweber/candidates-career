package com.allanweber.candidatescareer.app.applications.api;

import com.allanweber.candidatescareer.app.applications.dto.ApplicationResponse;
import com.allanweber.candidatescareer.app.candidate.dto.ResumeResponse;
import com.allanweber.candidatescareer.core.constants.ConstantsUtils;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Api(tags = "Recruiter Application Api")
@RequestMapping("/application")
public interface RecruiterApplicationApi {

    String CANDIDATE_ID = "candidateId";
    String VACANCY_ID = "vacancyId";
    String CANDIDATE_ID_DESCRIPTION = "candidate id";
    String VACANCY_ID_DESCRIPTION = "vacancy id";
    String CANDIDATE_NOT_FOUND = "Could not find the candidate";
    String LIST = "List";

    @ApiOperation(notes = "Send application link for candidate", value = "Send application link for candidate", response = ResumeResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Registration sent"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = CANDIDATE_NOT_FOUND)})
    @PostMapping("/{candidateId}/send-application/{vacancyId}")
    ResponseEntity<ApplicationResponse> sendApplication(
            @ApiParam(name = CANDIDATE_ID, value = CANDIDATE_ID_DESCRIPTION, required = true) @PathVariable(name = CANDIDATE_ID) String candidateId,
            @ApiParam(name = VACANCY_ID, value = VACANCY_ID_DESCRIPTION, required = true) @PathVariable(name = VACANCY_ID) String vacancyId);

    @ApiOperation(notes = "Get all applications sent to candidate", value = "Get all applications sent to candidate", response = ApplicationResponse.class,
            responseContainer = LIST)
    @ApiResponses({
            @ApiResponse(code = 200, message = "All applications"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = CANDIDATE_NOT_FOUND)})
    @GetMapping("/candidates/{candidateId}/applications")
    ResponseEntity<List<ApplicationResponse>> getCandidateApplications(
            @ApiParam(name = CANDIDATE_ID, value = CANDIDATE_ID_DESCRIPTION, required = true) @PathVariable(name = CANDIDATE_ID) String candidateId);

    @ApiOperation(notes = "Get all applications to some vacancy", value = "Get all applications to some vacancy", response = ApplicationResponse.class,
            responseContainer = LIST)
    @ApiResponses({
            @ApiResponse(code = 200, message = "All applications"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = CANDIDATE_NOT_FOUND)})
    @GetMapping("/vacancy/{vacancyId}/applications")
    ResponseEntity<List<ApplicationResponse>> getVacancyApplications(
            @ApiParam(name = VACANCY_ID, value = VACANCY_ID_DESCRIPTION, required = true) @PathVariable(name = VACANCY_ID) String vacancyId);
}
