package com.allanweber.candidatescareer.domain.candidate.mapper;

import com.allanweber.candidatescareer.domain.candidate.dto.CandidateRequest;
import com.allanweber.candidatescareer.domain.candidate.dto.CandidateResponse;
import com.allanweber.candidatescareer.domain.candidate.repository.Candidate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CandidateMapper {

    public static Candidate toEntity(CandidateRequest dto) {
        return Candidate
                .builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    public static CandidateResponse toResponse(Candidate entity) {
        return CandidateResponse
                .builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .socialEntries(entity.getSocialEntries())
                .socialNetwork(entity.getSocialNetwork())
                .build();
    }

    public static Candidate mapToUpdate(Candidate entity, CandidateRequest dto) {
        return entity.withName(dto.getName()).withEmail(dto.getEmail());
    }
}
