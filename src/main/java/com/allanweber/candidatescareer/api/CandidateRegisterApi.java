package com.allanweber.candidatescareer.api;

import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Api(tags = "Candidates Register Api")
@RequestMapping("/candidate-register")
public interface CandidateRegisterApi {

    String ID = "registerId";
    String ID_DESCRIPTION = "candidate register Id";
    String REGISTER_NOT_FOUND = "Could not find register";

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
    ResponseEntity<?> register(@ApiParam(name = ID, value = ID_DESCRIPTION, required = true) @PathVariable(name = ID) String registerId);
}
