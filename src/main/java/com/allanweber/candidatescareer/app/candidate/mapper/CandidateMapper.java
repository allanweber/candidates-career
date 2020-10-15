package com.allanweber.candidatescareer.app.candidate.mapper;

import com.allanweber.candidatescareer.app.candidate.dto.*;
import com.allanweber.candidatescareer.app.candidate.repository.Candidate;
import com.allanweber.candidatescareer.app.shared.CandidateExperience;
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

    public static Candidate toEntity(Candidate entity, CandidateProfile candidateProfile) {

        List<CandidateExperience> experiences = candidateProfile
                .getExperiences()
                .stream()
                .sorted(Comparator.comparing(CandidateExperience::getStart).reversed()).collect(Collectors.toList());

        return entity.withName(candidateProfile.getName())
                .withEmail(candidateProfile.getEmail())
                .withPhone(candidateProfile.getPhone())
                .withLocation(candidateProfile.getLocation())
                .withBio(candidateProfile.getBio())
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
                    .gitHubCandidate(entity.getGitHubCandidate())
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
