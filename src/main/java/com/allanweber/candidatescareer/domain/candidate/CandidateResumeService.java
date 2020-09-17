package com.allanweber.candidatescareer.domain.candidate;

import com.allanweber.candidatescareer.domain.auth.AuthService;
import com.allanweber.candidatescareer.domain.candidate.dto.ResumeResponse;
import com.allanweber.candidatescareer.domain.candidate.repository.CandidateMongoRepository;
import com.allanweber.candidatescareer.domain.candidate.repository.CandidateResume;
import com.allanweber.candidatescareer.domain.candidate.repository.CandidateResumeRepository;
import com.allanweber.candidatescareer.infrastructure.UploadResumeConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateResumeService {
    private static final String NOT_FOUND_MESSAGE = "Candidate not found.";
    private static final String FILE_TOO_LARGE_MESSAGE = "The file is too large %s kbs. The maximum allowed is %s kbs.";
    private static final String FILE_EXTENSION_NOT_ALLOWED_MESSAGE = "The file extension %s is not allowed. Upload a file with: %s";
    private static final String FILE_IO_MESSAGE = "There was an error unexpected to save the file.";

    private final CandidateMongoRepository candidateMongoRepository;
    private final CandidateResumeRepository candidateResumeRepository;
    private final AuthService authService;
    private final UploadResumeConfiguration uploadResumeConfiguration;

    public ResumeResponse uploadResume(String candidateId, MultipartFile file) {
        final String owner = authService.getUserName();
        candidateMongoRepository.findByIdAndOwner(candidateId, owner)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, NOT_FOUND_MESSAGE));

        if (file.getSize() > uploadResumeConfiguration.getMaxSize()) {
            throw new HttpClientErrorException(BAD_REQUEST, String.format(
                    FILE_TOO_LARGE_MESSAGE, file.getSize() / 1000, uploadResumeConfiguration.getMaxSize() / 1000
            ));
        }

        String fileExtension = FilenameUtils.getExtension(Objects.requireNonNull(file.getOriginalFilename()));
        if (!uploadResumeConfiguration.getExtensionsAllowed().contains(fileExtension)) {
            throw new HttpClientErrorException(BAD_REQUEST, String.format(
                    FILE_EXTENSION_NOT_ALLOWED_MESSAGE, fileExtension, String.join(",", uploadResumeConfiguration.getExtensionsAllowed())
            ));
        }

        String base64File;
        try {
            base64File = Base64.getEncoder().encodeToString(file.getBytes());
        } catch (IOException e) {
            log.error("Error to base64 file {} ", file.getOriginalFilename(), e);
            throw (HttpClientErrorException)new HttpClientErrorException(INTERNAL_SERVER_ERROR, FILE_IO_MESSAGE).initCause(e);
        }

        candidateResumeRepository.findByCandidateIdAndOwner(candidateId, owner)
                .ifPresent(candidateResumeRepository::delete);

        CandidateResume resume = CandidateResume.builder()
                .owner(owner)
                .candidateId(candidateId)
                .fileName(file.getOriginalFilename())
                .fileExtension(fileExtension)
                .fileSize(file.getSize())
                .file(base64File)
                .build();

        candidateResumeRepository.save(resume);
        return ResumeResponse.builder().fileName(file.getOriginalFilename()).build();
    }
}
