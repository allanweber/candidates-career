package com.allanweber.candidatescareer.domain.candidate.mapper;

import com.allanweber.candidatescareer.domain.candidate.dto.CandidateDto;
import com.allanweber.candidatescareer.domain.candidate.repository.Candidate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CandidateMapper {

    @Mapping(target = "id", source = "id")
    Candidate toEntity(CandidateDto dto);

    @Mapping(target = "id", source = "id")
    CandidateDto toDto(Candidate entity);

    @Mapping(target = "id", source = "id")
    Candidate mapToUpdate(String id, CandidateDto dto);
}
