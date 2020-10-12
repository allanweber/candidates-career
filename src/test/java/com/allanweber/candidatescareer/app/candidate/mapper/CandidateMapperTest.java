package com.allanweber.candidatescareer.app.candidate.mapper;

import com.allanweber.candidatescareer.app.candidate.dto.*;
import com.allanweber.candidatescareer.app.candidate.repository.Candidate;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CandidateMapperTest {

    @Test
    void toEntity() {
        CandidateRequest dto = CandidateRequest.builder().email("mail@mail.com").name("allan").id("id").build();

        Candidate candidate = CandidateMapper.toEntity(dto);
        assertEquals(dto.getEmail(), candidate.getEmail());
        assertEquals(dto.getName(), candidate.getName());
        assertNull(candidate.getId());
        assertNull(candidate.getSocialEntries());
        assertNull(candidate.getSocialNetwork());
        assertNull(candidate.getImage());
    }

    @Test
    void toEntity_null_request() {
        Candidate candidate = CandidateMapper.toEntity(null);
        assertNull(candidate.getEmail());
        assertNull(candidate.getName());
        assertNull(candidate.getId());
        assertNull(candidate.getSocialEntries());
        assertNull(candidate.getSocialNetwork());
        assertNull(candidate.getImage());
    }

    @Test
    void toResponse() {
        Candidate candidate = Candidate.builder()
                .id("id")
                .name("allan")
                .email("mail@mail.com")
                .image("image")
                .socialNetwork(Arrays.asList(
                        SocialNetworkDto.builder().url("linkedIn").type(SocialNetworkType.LINKEDIN).build(),
                        SocialNetworkDto.builder().url("website").type(SocialNetworkType.WEBSITE).build()
                ))
                .socialEntries(Arrays.asList(
                        SocialEntry.builder().status(SocialStatus.GRANTED).type(SocialNetworkType.LINKEDIN).build(),
                        SocialEntry.builder().status(SocialStatus.DENIED).type(SocialNetworkType.GITHUB).build()
                ))
                .build();

        CandidateResponse response = CandidateMapper.toResponse(candidate);
        assertEquals(candidate.getId(), response.getId());
        assertEquals(candidate.getName(), response.getName());
        assertEquals(candidate.getEmail(), response.getEmail());
        assertEquals(candidate.getSocialNetwork(), response.getSocialNetwork());
        assertEquals(candidate.getSocialEntries(), response.getSocialEntries());
    }

    @Test
    void toResponse_null_entity() {
        CandidateResponse response = CandidateMapper.toResponse(null);
        assertNull(response.getId());
        assertNull(response.getName());
        assertNull(response.getEmail());
        assertNull(response.getSocialNetwork());
        assertNull(response.getSocialEntries());
    }

    @Test
    void toResponse_basic_properties() {
        Candidate candidate = Candidate.builder()
                .id("id")
                .name("allan")
                .email("mail@mail.com")
                .build();

        CandidateResponse response = CandidateMapper.toResponse(candidate);
        assertEquals(candidate.getId(), response.getId());
        assertEquals(candidate.getName(), response.getName());
        assertEquals(candidate.getEmail(), response.getEmail());
        assertNull(response.getSocialNetwork());
        assertNull(response.getSocialEntries());
    }

    @Test
    void mapToUpdate(){
        Candidate candidate = Candidate.builder()
                .id("id")
                .name("allan")
                .email("mail@mail.com")
                .image("image")
                .socialNetwork(Arrays.asList(
                        SocialNetworkDto.builder().url("linkedIn").type(SocialNetworkType.LINKEDIN).build(),
                        SocialNetworkDto.builder().url("website").type(SocialNetworkType.WEBSITE).build()
                ))
                .socialEntries(Arrays.asList(
                        SocialEntry.builder().status(SocialStatus.GRANTED).type(SocialNetworkType.LINKEDIN).build(),
                        SocialEntry.builder().status(SocialStatus.DENIED).type(SocialNetworkType.GITHUB).build()
                ))
                .build();

        CandidateUpdate dto = CandidateUpdate.builder().name("name").email("mail").bio("bio").currentCompany("cia").location("local").build();

        Candidate toUpdate = CandidateMapper.mapToUpdate(candidate, dto);
        assertEquals(toUpdate.getId(), candidate.getId());
        assertEquals(toUpdate.getName(), dto.getName());
        assertEquals(toUpdate.getEmail(), dto.getEmail());
        assertEquals(toUpdate.getSocialNetwork(), candidate.getSocialNetwork());
        assertEquals(toUpdate.getSocialEntries(), candidate.getSocialEntries());
        assertEquals(toUpdate.getImage(), candidate.getImage());
    }
}