package com.allanweber.candidatescareer.api;

import com.allanweber.candidatescareer.domain.candidate.CandidateResumeService;
import com.allanweber.candidatescareer.domain.candidate.CandidateService;
import com.allanweber.candidatescareer.domain.candidate.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpStatus.GONE;
import static org.springframework.http.ResponseEntity.*;

@RequiredArgsConstructor
@RestController
public class CandidateController implements CandidateApi {

    private final CandidateService candidateService;
    private final CandidateResumeService resumeService;

    @Override
    public ResponseEntity<List<CandidateResponse>> getAll() {
        return ok(candidateService.getAll());
    }

    @Override
    public ResponseEntity<CandidateResponse> get(String id) {
        return ok(candidateService.getById(id));
    }

    @Override
    public ResponseEntity<CandidateResponse> create(@Valid CandidateRequest body) {
        CandidateResponse created = candidateService.insert(body);
        return created(URI.create(String.format("/candidates/%s", created.getId()))).body(created);
    }

    @Override
    public ResponseEntity<CandidateResponse> update(String id, @Valid CandidateUpdate body) {
        return ok(candidateService.update(id, body));
    }

    @Override
    public ResponseEntity<?> delete(String id) {
        candidateService.delete(id);
        return status(GONE).build();
    }

    @Override
    public ResponseEntity<List<SocialEntry>> addSocialEntry(String id, @Valid List<SocialNetworkType> networkTypes) {
        return ok(candidateService.addSocialEntries(id, networkTypes));
    }

    @Override
    public ResponseEntity<String> image(String id) {
        return ok(candidateService.getImage(id));
    }

    @Override
    public ResponseEntity<ResumeResponse> uploadResume(String id, MultipartFile file) {
        return ok(resumeService.uploadResume(id, file));
    }

    @Override
    public ResponseEntity<byte[]> getResume(String id) {
        return ok(resumeService.getResumeFile(id));
    }

    @Override
    public ResponseEntity<ResumeResponse> getResumeInfo(String id) {
        return ok(resumeService.getResumeInfo(id));
    }
}
