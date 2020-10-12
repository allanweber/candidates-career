package com.allanweber.candidatescareer.app.candidate.dto;

import com.allanweber.candidatescareer.app.social.dto.GitHubCandidate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CandidateResponse {

    private String id;

    private String name;

    private String email;

    private String owner;

    private String location;

    private String bio;

    private String currentCompany;

    private String phone;

    private List<SocialNetworkDto> socialNetwork;

    private List<SocialEntry> socialEntries;

    private LocalDateTime lastUpdate;

    private GitHubCandidate gitHubCandidate;
}
