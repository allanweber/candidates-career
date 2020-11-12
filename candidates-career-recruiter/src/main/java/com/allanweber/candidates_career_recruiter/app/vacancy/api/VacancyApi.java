package com.allanweber.candidates_career_recruiter.app.vacancy.api;

import com.allanweber.candidates_career_recruiter.app.vacancy.dto.VacancyDto;
import com.allanweber.candidates_career_recruiter.core.constants.ConstantsUtils;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "Vacancies Api")
@RequestMapping("/vacancies")
public interface VacancyApi {

    String ID = "vacancyId";

    @ApiOperation(notes = "Return all vacancies", value = "Return all vacancies", response = VacancyDto.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Vacancies returned"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE)})
    @GetMapping
    ResponseEntity<List<VacancyDto>> getAll();

    @ApiOperation(notes = "Return a vacancy by id", value = "Return a vacancy", response = VacancyDto.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Vacancy returned"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = "Could not find the vacancy")})
    @GetMapping("/{vacancyId}")
    ResponseEntity<VacancyDto> get(@ApiParam(name = ID, value = "Vacancy id", required = true) @PathVariable(name = ID) String id);

    @ApiOperation(notes = "Create a new vacancy based", value = "Create new vacancy", response = VacancyDto.class)
    @ApiResponses({
            @ApiResponse(code = 201, message = "Vacancy created"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE)})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    ResponseEntity<VacancyDto> create(@Valid @RequestBody VacancyDto body);

    @ApiOperation(notes = "Update the vacancy by id", value = "Update a vacancy", response = VacancyDto.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Vacancy updated"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = "Could not find the vacancy")})
    @PutMapping("/{vacancyId}")
    ResponseEntity<VacancyDto> update(@ApiParam(name = ID, value = "Vacancy id", required = true) @PathVariable(name = ID) String id,
                                      @Valid @RequestBody VacancyDto body);

    @ApiOperation(notes = "Delete the vacancy by id", value = "Delete a vacancy")
    @ApiResponses({
            @ApiResponse(code = 410, message = "Vacancy deleted"),
            @ApiResponse(code = 400, message = ConstantsUtils.HTTP_400_MESSAGE),
            @ApiResponse(code = 404, message = "Could not find the vacancy")})
    @ResponseStatus(HttpStatus.GONE)
    @DeleteMapping("/{vacancyId}")
    ResponseEntity<?> delete(@ApiParam(name = ID, value = "Vacancy id", required = true) @PathVariable(name = ID) String id);
}
