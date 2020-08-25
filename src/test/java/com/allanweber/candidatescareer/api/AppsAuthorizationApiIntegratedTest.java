package com.allanweber.candidatescareer.api;

import com.allanweber.candidatescareer.domain.candidate.CandidateService;
import com.allanweber.candidatescareer.domain.candidate.dto.CandidateRequest;
import com.allanweber.candidatescareer.domain.candidate.dto.CandidateResponse;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType;
import com.allanweber.candidatescareer.domain.candidate.repository.CandidateRepository;
import com.allanweber.candidatescareer.domain.helper.ObjectMapperHelper;
import com.allanweber.candidatescareer.domain.linkedin.dto.LinkedInProfile;
import com.allanweber.candidatescareer.infrastructure.handler.dto.ResponseErrorDto;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("inttest")
class AppsAuthorizationApiIntegratedTest {

    private static final String LINKEDIN_AUTH_PATH = "/social-authorization/%s/linkedin";
    private static final String LINKEDIN_CALL_BACK = "/auth/callback?code=123456&state=%s";

    private final ObjectReader responseErrorDtoReader = ObjectMapperHelper.get().readerFor(ResponseErrorDto.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CandidateRepository repository;

    @Autowired
    private CandidateService candidateService;

    @BeforeEach
    public void setUp() {
        repository.findAll().forEach(repository::delete);
    }

    @Test
    void test_social_network_request_is_invalid_linkedIn_auth() throws Exception {
        CandidateResponse candidate = candidateService.insert(CandidateRequest.builder().email("mail@gmail.com").name("name").build());

        var postResponse = mockMvc.perform(get(String.format(LINKEDIN_AUTH_PATH, candidate.getId()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        ResponseErrorDto responseErrorDto = responseErrorDtoReader.readValue(postResponse);
        assertEquals("Social network request is invalid", responseErrorDto.getMessage());

        candidateService.delete(candidate.getId());
    }

    @Test
    void test_social_network_request_was_completed_linkedIn_auth() throws Exception {
        CandidateResponse candidate = candidateService.insert(
                CandidateRequest.builder()
                        .email("mail@gmail.com")
                        .name("name").build()
        );

        candidateService.addSocialEntries(candidate.getId(), Collections.singletonList(SocialNetworkType.LINKEDIN));
        candidateService.saveLinkedInData(candidate.getId(), new LinkedInProfile("Allan", "Weber", "image"));

        var postResponse = mockMvc.perform(get(String.format(LINKEDIN_AUTH_PATH, candidate.getId()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        ResponseErrorDto responseErrorDto = responseErrorDtoReader.readValue(postResponse);
        assertEquals("Social network request was completed", responseErrorDto.getMessage());

        candidateService.delete(candidate.getId());
    }

    @Test
    void test_social_network_request_was_completed_linkedIn_callback() throws Exception {
        CandidateResponse candidate = candidateService.insert(
                CandidateRequest.builder()
                        .email("mail@gmail.com")
                        .name("name").build()
        );

        candidateService.addSocialEntries(candidate.getId(), Collections.singletonList(SocialNetworkType.LINKEDIN));
        candidateService.saveLinkedInData(candidate.getId(), new LinkedInProfile("Allan", "Weber", "image"));

        var postResponse = mockMvc.perform(get(String.format(LINKEDIN_CALL_BACK, candidate.getId()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        ResponseErrorDto responseErrorDto = responseErrorDtoReader.readValue(postResponse);
        assertEquals("Social network request was completed", responseErrorDto.getMessage());

        candidateService.delete(candidate.getId());
    }
}