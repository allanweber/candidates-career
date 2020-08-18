package com.allanweber.candidatescareer.api;

import com.allanweber.candidatescareer.domain.candidate.CandidateService;
import com.allanweber.candidatescareer.domain.candidate.dto.CandidateDto;
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
public class CandidateController implements CandidateApi {

    private final CandidateService service;

    @Override
    public ResponseEntity<List<CandidateDto>> getAll() {
        return ok(service.getAll());
    }

    @Override
    public ResponseEntity<CandidateDto> get(String id) {
        return ok(service.getById(id));
    }

    @Override
    public ResponseEntity<CandidateDto> create(@Valid CandidateDto body) {
        CandidateDto created = service.insert(body);
        return created(URI.create(String.format("/candidates/%s", created.getId()))).body(created);
    }

    @Override
    public ResponseEntity<CandidateDto> update(String id, @Valid CandidateDto body) {
        return ok(service.update(id, body));
    }

    @Override
    public ResponseEntity<?> delete(String id) {
        service.delete(id);
        return status(GONE).build();
    }
}
