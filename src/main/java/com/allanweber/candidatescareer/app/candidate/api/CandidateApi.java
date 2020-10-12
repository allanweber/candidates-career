package com.allanweber.candidatescareer.app.candidate.api;

import com.allanweber.candidatescareer.app.candidate.dto.*;
import com.allanweber.candidatescareer.app.helper.ConstantsUtils;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "Candidates Api")
@RequestMapping("/candidates")
public interface CandidateApi {

    String CANDIDATE_ID = "candidateId";
    String CANDIDATE_ID_DESCRIPTION = "candidate id";
    String CANDIDATE_NOT_FOUND = "Could not find the candidate";
    String LIST = "List";

    @ApiOperation(notes = "Return all Candidates", value = "Return all Candidates", response = CandidateResponse.class,
            responseContainer = LIST)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Candidates returned"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE)})
    @GetMapping
    ResponseEntity<List<CandidateResponse>> getAll();

    @ApiOperation(notes = "Return a candidate by id", value = "Return a candidate", response = CandidateResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "candidate returned"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = CANDIDATE_NOT_FOUND)})
    @GetMapping("/{candidateId}")
    ResponseEntity<CandidateResponse> get(@ApiParam(name = CANDIDATE_ID, value = CANDIDATE_ID_DESCRIPTION, required = true) @PathVariable(name = CANDIDATE_ID) String id);

    @ApiOperation(notes = "Return a candidate full profile by id", value = "Return a candidate full profile", response = CandidateResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "candidate full profile returned"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = CANDIDATE_NOT_FOUND)})
    @GetMapping("/{candidateId}/profile")
    ResponseEntity<CandidateProfile> getProfile(@ApiParam(name = CANDIDATE_ID, value = CANDIDATE_ID_DESCRIPTION, required = true) @PathVariable(name = CANDIDATE_ID) String id);


    @ApiOperation(notes = "Create a new candidate based", value = "Create new candidate", response = CandidateResponse.class)
    @ApiResponses({
            @ApiResponse(code = 201, message = "candidate created"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE)})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    ResponseEntity<CandidateResponse> create(@Valid @RequestBody CandidateRequest body);

    @ApiOperation(notes = "Update the candidate by id", value = "Update a candidate", response = CandidateResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "candidate updated"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = CANDIDATE_NOT_FOUND)})
    @PutMapping("/{candidateId}")
    ResponseEntity<CandidateResponse> update(@ApiParam(name = CANDIDATE_ID, value = CANDIDATE_ID_DESCRIPTION, required = true) @PathVariable(name = CANDIDATE_ID) String id,
                                             @Valid @RequestBody CandidateUpdate body);

    @ApiOperation(notes = "Delete the candidate by id", value = "Delete a candidate")
    @ApiResponses({
            @ApiResponse(code = 410, message = "candidate deleted"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = CANDIDATE_NOT_FOUND)})
    @ResponseStatus(HttpStatus.GONE)
    @DeleteMapping("/{candidateId}")
    ResponseEntity<?> delete(@ApiParam(name = CANDIDATE_ID, value = CANDIDATE_ID_DESCRIPTION, required = true) @PathVariable(name = CANDIDATE_ID) String id);

    @ApiOperation(notes = "Add requests to social entries for candidate by id", value = "Add social entries a candidate", response = SocialEntry.class,
            responseContainer = LIST)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Candidate updated"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = CANDIDATE_NOT_FOUND)})
    @PutMapping("/{candidateId}/social-entry")
    ResponseEntity<List<SocialEntry>> addSocialEntry(@ApiParam(name = CANDIDATE_ID, value = CANDIDATE_ID_DESCRIPTION, required = true) @PathVariable(name = CANDIDATE_ID) String id,
                                                     @Valid @RequestBody List<SocialNetworkType> networkTypes);

    @ApiOperation(notes = "Return candidate image by id", value = "Candidate Image")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Candidate image"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = CANDIDATE_NOT_FOUND)})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{candidateId}/image")
    ResponseEntity<String> image(@ApiParam(name = CANDIDATE_ID, value = CANDIDATE_ID_DESCRIPTION, required = true) @PathVariable(name = CANDIDATE_ID) String id);

    @ApiOperation(notes = "Upload resume file for candidate by id", value = "Upload resume file for candidate", response = ResumeResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Resume uploaded"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = CANDIDATE_NOT_FOUND)})
    @PostMapping("/{candidateId}/resume-upload")
    ResponseEntity<ResumeResponse> uploadResume(@ApiParam(name = CANDIDATE_ID, value = CANDIDATE_ID_DESCRIPTION, required = true) @PathVariable(name = CANDIDATE_ID) String id,
                                                   @RequestParam("file") MultipartFile file);

    @ApiOperation(notes = "Get resume file for candidate by id", value = "Get resume file for candidate", response = byte.class, responseContainer = LIST)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Get Resume"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = CANDIDATE_NOT_FOUND)})
    @GetMapping("/{candidateId}/resume")
    ResponseEntity<byte[]> getResume(@ApiParam(name = CANDIDATE_ID, value = CANDIDATE_ID_DESCRIPTION, required = true) @PathVariable(name = CANDIDATE_ID) String id);

    @ApiOperation(notes = "Get resume information for candidate by id", value = "Get resume information for candidate", response = ResumeResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Get Resume information"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = CANDIDATE_NOT_FOUND)})
    @GetMapping("/{candidateId}/resume-info")
    ResponseEntity<ResumeResponse> getResumeInfo(@ApiParam(name = CANDIDATE_ID, value = CANDIDATE_ID_DESCRIPTION, required = true) @PathVariable(name = CANDIDATE_ID) String id);



}
