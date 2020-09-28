package com.allanweber.candidatescareer.domain.candidate.mapper;

import com.allanweber.candidatescareer.domain.candidate.dto.CandidateRegisterResponse;
import com.allanweber.candidatescareer.domain.candidate.repository.CandidateRegister;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CandidateRegisterMapper {

    public static CandidateRegisterResponse toResponse(CandidateRegister entity) {
        return CandidateRegisterResponse.builder().error(entity.getError()).status(entity.getStatus()).build();
    }
}
