package com.allanweber.candidatescareer.api;

import com.allanweber.candidatescareer.domain.candidate.dto.CandidateRequest;
import com.allanweber.candidatescareer.domain.candidate.dto.CandidateResponse;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "Candidates Api")
@RequestMapping("/candidates")
public interface CandidateApi {

    String ID = "candidateId";
    String ID_DESCRIPTION = "candidate id";
    String CANDIDATE_NOT_FOUND = "Could not find the candidate";

    @ApiOperation(notes = "Return all Candidates", value = "Return all Candidates", response = CandidateResponse.class)
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
    ResponseEntity<CandidateResponse> get(@ApiParam(name = ID, value = ID_DESCRIPTION, required = true) @PathVariable(name = ID) String id);

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
    ResponseEntity<CandidateResponse> update(@ApiParam(name = ID, value = ID_DESCRIPTION, required = true) @PathVariable(name = ID) String id,
                                            @Valid @RequestBody CandidateRequest body);

    @ApiOperation(notes = "Delete the candidate by id", value = "Delete a candidate")
    @ApiResponses({
            @ApiResponse(code = 410, message = "candidate deleted"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = CANDIDATE_NOT_FOUND)})
    @ResponseStatus(HttpStatus.GONE)
    @DeleteMapping("/{candidateId}")
    ResponseEntity<?> delete(@ApiParam(name = ID, value = ID_DESCRIPTION, required = true) @PathVariable(name = ID) String id);

    @ApiOperation(notes = "Add requests to social entries for candidate by id", value = "Add social entries a candidate", response = CandidateResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "candidate updated"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = CANDIDATE_NOT_FOUND)})
    @PutMapping("/{candidateId}/social-entry")
    ResponseEntity<CandidateResponse> addSocialEntry(@ApiParam(name = ID, value = ID_DESCRIPTION, required = true) @PathVariable(name = ID) String id,
                                             @Valid @RequestBody List<SocialNetworkType> networkTypes);

    @ApiOperation(notes = "Return candidate image by id", value = "Candidate Image")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Candidate image"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = CANDIDATE_NOT_FOUND)})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{candidateId}/image")
    ResponseEntity<String> image(@ApiParam(name = ID, value = ID_DESCRIPTION, required = true) @PathVariable(name = ID) String id);
}
