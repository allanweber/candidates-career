package com.allanweber.candidatescareer.app.social.api;

import com.allanweber.candidatescareer.app.candidate.dto.SocialNetworkType;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Api(tags = "Social Network Authorizations")
@RequestMapping("/")
public interface SocialApi {

    String CANDIDATE_ID = "candidateId";

    @ApiOperation(notes = "Redirect candidate to LinkedIn authorization", value = "Redirect to LinkedIn authorization")
    @ApiResponses({
            @ApiResponse(code = 302, message = "Candidate redirected"),
            @ApiResponse(code = 400, message = "Social network request is invalid"),
            @ApiResponse(code = 501, message = "Social network request is not implemented"),
            @ApiResponse(code = 404, message = "Social network request not found")})
    @GetMapping("/social-authorization/{candidateId}/linkedin")
    ResponseEntity<Void> socialAuthorizationLinkedIn(@ApiParam(name = CANDIDATE_ID, value = "candidate id", required = true)
                                                     @PathVariable(name = CANDIDATE_ID) String candidateId);

    @GetMapping("/auth/callback")
    ResponseEntity<Void> linkedInCallback(@RequestParam("code") String authorizationCode, @RequestParam("state") String state);

    @ApiOperation(notes = "Redirect candidate to GitHub authorization", value = "Redirect to GitHub authorization")
    @ApiResponses({
            @ApiResponse(code = 302, message = "Candidate redirected"),
            @ApiResponse(code = 400, message = "Social network request is invalid"),
            @ApiResponse(code = 501, message = "Social network request is not implemented"),
            @ApiResponse(code = 404, message = "Social network request not found")})
    @GetMapping("/social-authorization/{candidateId}/github")
    ResponseEntity<Void> socialAuthorizationGitHub(@ApiParam(name = CANDIDATE_ID, value = "candidate id", required = true)
                                                   @PathVariable(name = CANDIDATE_ID) String candidateId);

    @GetMapping("/auth/callback/github")
    ResponseEntity<Void> githubCallback(@RequestParam("code") String authorizationCode, @RequestParam("state") String state);

    @ApiOperation(notes = "Deny social network access", value = "Deny social network access")
    @ApiResponses({
            @ApiResponse(code = 302, message = "Social network access denied"),
            @ApiResponse(code = 400, message = "Social network access  is invalid"),
            @ApiResponse(code = 404, message = "Social network access  not found")})
    @GetMapping("/social-authorization/{candidateId}/{network}/deny")
    ResponseEntity<Void> denySocialAuthorization(@ApiParam(name = CANDIDATE_ID, value = "candidate id", required = true)
                                                 @PathVariable(name = CANDIDATE_ID) String candidateId,
                                                 @ApiParam(name = "network", value = "social network type", required = true)
                                                 @PathVariable(name = "network") SocialNetworkType network);
}
