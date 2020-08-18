package com.allanweber.candidatescareer.api;

import com.allanweber.candidatescareer.domain.candidate.dto.CandidateDto;
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

    @ApiOperation(notes = "Return all Candidates", value = "Return all Candidates", response = CandidateDto.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Candidates returned"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE)})
    @GetMapping
    ResponseEntity<List<CandidateDto>> getAll();

    @ApiOperation(notes = "Return a candidate by id", value = "Return a candidate", response = CandidateDto.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "candidate returned"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = "Could not find the candidate")})
    @GetMapping("/{candidateId}")
    ResponseEntity<CandidateDto> get(@ApiParam(name = ID, value = "candidate id", required = true) @PathVariable(name = ID) String id);

    @ApiOperation(notes = "Create a new candidate based", value = "Create new candidate", response = CandidateDto.class)
    @ApiResponses({
            @ApiResponse(code = 201, message = "candidate created"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE)})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    ResponseEntity<CandidateDto> create(@Valid @RequestBody CandidateDto body);

    @ApiOperation(notes = "Update the candidate by id", value = "Update a candidate", response = CandidateDto.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "candidate updated"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = "Could not find the candidate")})
    @PutMapping("/{candidateId}")
    ResponseEntity<CandidateDto> update(@ApiParam(name = ID, value = "candidate id", required = true) @PathVariable(name = ID) String id,
                                        @Valid @RequestBody CandidateDto body);

    @ApiOperation(notes = "Delete the candidate by id", value = "Delete a candidate")
    @ApiResponses({
            @ApiResponse(code = 410, message = "candidate deleted"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = "Could not find the candidate")})
    @ResponseStatus(HttpStatus.GONE)
    @DeleteMapping("/{candidateId}")
    ResponseEntity<?> delete(@ApiParam(name = ID, value = "candidate id", required = true) @PathVariable(name = ID) String id);
}
