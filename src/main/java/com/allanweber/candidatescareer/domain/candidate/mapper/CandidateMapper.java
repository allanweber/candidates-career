package com.allanweber.candidatescareer.domain.candidate.mapper;

import com.allanweber.candidatescareer.domain.candidate.dto.*;
import com.allanweber.candidatescareer.domain.candidate.repository.Candidate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CandidateMapper {

    public static Candidate toEntity(CandidateRequest dto) {
        CandidateRequest candidateRequest = Optional.ofNullable(dto).orElse(new CandidateRequest());
        return Candidate
                .builder()
                .name(candidateRequest.getName())
                .email(candidateRequest.getEmail())
                .phone(candidateRequest.getPhone())
                .build();
    }

    public static Candidate toEntity(Candidate entity, CandidateProfile registerProfile) {

        List<CandidateExperience> experiences = registerProfile
                .getExperiences()
                .stream()
                .sorted(Comparator.comparing(CandidateExperience::getStart).reversed()).collect(Collectors.toList());

        return entity.withName(registerProfile.getName())
                .withEmail(registerProfile.getEmail())
                .withPhone(registerProfile.getPhone())
                .withLocation(registerProfile.getLocation())
                .withBio(registerProfile.getBio())
                .withExperiences(experiences)
                .withCurrentCompany(experiences.stream().findFirst().map(CandidateExperience::getCompanyName).orElse(null))
                .withLastUpdate(LocalDateTime.now(ZoneOffset.UTC));
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
                    .phone(entity.getPhone())
                    .owner(entity.getOwner())
                    .socialEntries(entity.getSocialEntries())
                    .socialNetwork(entity.getSocialNetwork())
                    .location(entity.getLocation())
                    .bio(entity.getBio())
                    .currentCompany(entity.getCurrentCompany())
                    .lastUpdate(entity.getLastUpdate())
                    .build();
        }
        return response;
    }

    public static Candidate mapToUpdate(Candidate entity, CandidateUpdate dto) {

        List<CandidateExperience> experiences = Optional.ofNullable(entity
                .getExperiences())
                .orElse(Collections.emptyList())
                .stream()
                .sorted(Comparator.comparing(CandidateExperience::getStart).reversed()).collect(Collectors.toList());

        return entity.withName(dto.getName())
                .withEmail(dto.getEmail())
                .withPhone(dto.getPhone())
                .withLocation(dto.getLocation())
                .withBio(dto.getBio())
                .withCurrentCompany(experiences.stream().findFirst().map(CandidateExperience::getCompanyName).orElse(dto.getCurrentCompany()));
    }
}
