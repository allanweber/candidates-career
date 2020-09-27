package com.allanweber.candidatescareer.domain.candidate.mapper;

import com.allanweber.candidatescareer.domain.candidate.dto.CandidateRequest;
import com.allanweber.candidatescareer.domain.candidate.dto.CandidateResponse;
import com.allanweber.candidatescareer.domain.candidate.dto.CandidateUpdate;
import com.allanweber.candidatescareer.domain.candidate.repository.Candidate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CandidateMapper {

    public static Candidate toEntity(CandidateRequest dto) {
        CandidateRequest candidateRequest = Optional.ofNullable(dto).orElse(new CandidateRequest());
        return Candidate
                .builder()
                .name(candidateRequest.getName())
                .email(candidateRequest.getEmail())
                .build();
    }

    public static CandidateResponse toResponse(Candidate entity) {
        CandidateResponse response;
        if (Objects.isNull(entity)) {
            response = CandidateResponse.builder().build();
        } else {
            response = CandidateResponse
                    .builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .email(entity.getEmail())
                    .socialEntries(entity.getSocialEntries())
                    .socialNetwork(entity.getSocialNetwork())
                    .location(entity.getLocation())
                    .bio(entity.getBio())
                    .currentCompany(entity.getCurrentCompany())
                    .build();
        }
        return response;
    }

    public static Candidate mapToUpdate(Candidate entity, CandidateUpdate dto) {
        return entity.withName(dto.getName())
                .withEmail(dto.getEmail())
                .withLocation(dto.getLocation())
                .withCurrentCompany(dto.getCurrentCompany())
                .withBio(dto.getBio());
    }
}
