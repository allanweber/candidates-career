package com.allanweber.candidatescareer.app.candidate_repositories.api;

import com.allanweber.candidatescareer.app.candidate_repositories.dto.GithubRepository;
import com.allanweber.candidatescareer.app.candidate_repositories.dto.RepositoryCounter;
import com.allanweber.candidatescareer.core.constants.ConstantsUtils;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Api(tags = "Candidates Repositories Api")
@RequestMapping("/candidates/{candidateId}/repositories")
public interface CandidateRepositoriesApi {

    String ID = "candidateId";
    String ID_DESCRIPTION = "candidate id";
    String CANDIDATE_NOT_FOUND = "Could not find the candidate";

    @ApiOperation(notes = "Count Candidate repositories", value = "Count Candidate repositories", response = Integer.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Quantity returned"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = CANDIDATE_NOT_FOUND)})
    @GetMapping("/count")
    ResponseEntity<RepositoryCounter> count(@ApiParam(name = ID, value = ID_DESCRIPTION, required = true) @PathVariable(name = ID) String id);

    @ApiOperation(notes = "Get all paged Candidate repositories", value = "Get all paged Candidate repositories", response = Integer.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Repositories returned"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = CANDIDATE_NOT_FOUND)})
    @GetMapping("")
    ResponseEntity<List<GithubRepository>> getRepositories(@ApiParam(name = ID, value = ID_DESCRIPTION, required = true) @PathVariable(name = ID) String id,
                                                           @ApiParam(name = "size", example = "10", defaultValue = "10") @RequestParam(name = "size", required = false) int size,
                                                           @ApiParam(name = "offset", example = "0", defaultValue = "0") @RequestParam(name = "offset", required = false) int offset,
                                                           @ApiParam(name = "sort", defaultValue = "name") @RequestParam(name = "sort", required = false) String sort);
}
