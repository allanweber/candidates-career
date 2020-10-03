package com.allanweber.candidatescareer.domain.candidate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private List<SocialNetworkDto> socialNetwork;

    private List<SocialEntry> socialEntries;
}
