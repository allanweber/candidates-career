package com.allanweber.candidatescareer.api;

import com.allanweber.candidatescareer.domain.candidate.CandidateService;
import com.allanweber.candidatescareer.domain.candidate.dto.CandidateRequest;
import com.allanweber.candidatescareer.domain.candidate.dto.CandidateResponse;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialEntry;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType;
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
    public ResponseEntity<List<CandidateResponse>> getAll() {
        return ok(service.getAll());
    }

    @Override
    public ResponseEntity<CandidateResponse> get(String id) {
        return ok(service.getById(id));
    }

    @Override
    public ResponseEntity<CandidateResponse> create(@Valid CandidateRequest body) {
        CandidateResponse created = service.insert(body);
        return created(URI.create(String.format("/candidates/%s", created.getId()))).body(created);
    }

    @Override
    public ResponseEntity<CandidateResponse> update(String id, @Valid CandidateRequest body) {
        return ok(service.update(id, body));
    }

    @Override
    public ResponseEntity<?> delete(String id) {
        service.delete(id);
        return status(GONE).build();
    }

    @Override
    public ResponseEntity<List<SocialEntry>> addSocialEntry(String id, @Valid List<SocialNetworkType> networkTypes) {
        return ok(service.addSocialEntries(id, networkTypes));
    }

    @Override
    public ResponseEntity<String> image(String id) {
        return ok(service.getImage(id));
    }
}
