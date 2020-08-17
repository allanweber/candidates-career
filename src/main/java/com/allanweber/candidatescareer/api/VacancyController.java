package com.allanweber.candidatescareer.api;

import com.allanweber.candidatescareer.domain.vacancy.VacancyService;
import com.allanweber.candidatescareer.domain.vacancy.dto.VacancyDto;
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

    private final VacancyService vacancyService;

    @Override
    public ResponseEntity<List<VacancyDto>> getAll() {
        return ok(vacancyService.getAll());
    }

    @Override
    public ResponseEntity<VacancyDto> get(String vacancyId) {
        return ok(vacancyService.getById(vacancyId));
    }

    @Override
    public ResponseEntity<VacancyDto> create(@Valid VacancyDto body) {
        VacancyDto created = vacancyService.insert(body);
        return created(URI.create(String.format("/vacancies/%s", created.getId()))).body(created);
    }

    @Override
    public ResponseEntity<VacancyDto> update(String vacancyId, @Valid VacancyDto body) {
        return ok(vacancyService.update(vacancyId, body));
    }

    @Override
    public ResponseEntity<?> delete(String vacancyId) {
        vacancyService.delete(vacancyId);
        return status(GONE).build();
    }
}
