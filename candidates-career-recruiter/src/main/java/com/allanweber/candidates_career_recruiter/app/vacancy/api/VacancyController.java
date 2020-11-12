package com.allanweber.candidates_career_recruiter.app.vacancy.api;

import com.allanweber.candidates_career_recruiter.app.vacancy.dto.VacancyDto;
import com.allanweber.candidates_career_recruiter.app.vacancy.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpStatus.GONE;
import static org.springframework.http.ResponseEntity.*;

@RequiredArgsConstructor
@RestController
public class VacancyController implements VacancyApi {

    private final VacancyService service;

    @Override
    public ResponseEntity<List<VacancyDto>> getAll() {
        return ok(service.getAll());
    }

    @Override
    public ResponseEntity<VacancyDto> get(String id) {
        return ok(service.getById(id));
    }

    @Override
    public ResponseEntity<VacancyDto> create(@Valid VacancyDto body) {
        body.trim();
        VacancyDto created = service.insert(body);
        return created(URI.create(String.format("/vacancies/%s", created.getId()))).body(created);
    }

    @Override
    public ResponseEntity<VacancyDto> update(String id, @Valid VacancyDto body) {
        return ok(service.update(id, body));
    }

    @Override
    public ResponseEntity<?> delete(String id) {
        service.delete(id);
        return status(GONE).build();
    }
}
