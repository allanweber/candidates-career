package com.allanweber.candidates_career_recruiter.app.candidate.api;

import com.allanweber.candidates_career_recruiter.app.candidate.dto.*;
import com.allanweber.candidates_career_recruiter.app.candidate.service.CandidateResumeService;
import com.allanweber.candidates_career_recruiter.app.candidate.service.CandidateService;
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
    public ResponseEntity<CandidateProfile> getProfile(String id) {
        return ok(candidateService.getProfile(id));
    }

    @Override
    public ResponseEntity<CandidateResponse> create(@Valid CandidateRequest body) {
        body.trim();
        CandidateResponse created = candidateService.insert(body);
        return created(URI.create(String.format("/candidates/%s", created.getId()))).body(created);
    }

    @Override
    public ResponseEntity<CandidateResponse> update(String id, @Valid CandidateUpdate body) {
        body.trim();
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
