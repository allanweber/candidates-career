package com.allanweber.candidatescareer.api;

import com.allanweber.candidatescareer.domain.candidate.dto.RepositoryCounter;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
