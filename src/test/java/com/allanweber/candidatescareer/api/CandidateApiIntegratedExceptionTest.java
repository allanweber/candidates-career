package com.allanweber.candidatescareer.api;

import com.allanweber.candidatescareer.api.helpers.CandidateApiTestHelper;
import com.allanweber.candidatescareer.domain.candidate.dto.CandidateRequest;
import com.allanweber.candidatescareer.domain.candidate.dto.CandidateResponse;
import com.allanweber.candidatescareer.domain.candidate.repository.CandidateRepository;
import com.allanweber.candidatescareer.infrastructure.handler.dto.ResponseErrorDto;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("inttest")
public class CandidateApiIntegratedExceptionTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CandidateRepository repository;

    private CandidateApiTestHelper testHelper;

    @BeforeEach
    public void setUp() {
        this.testHelper = new CandidateApiTestHelper(repository, mockMvc);
        this.testHelper.deleteAll();
    }

    @Test
    void invalid_body_exception() throws Exception {
        var bodyJson = testHelper.candidateRequestJson(CandidateRequest.builder().email("email@mail.com").build());
        var errorResponse = mockMvc.perform(post(CandidateApiTestHelper.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        ResponseErrorDto errorDto = testHelper.jsonToResponseErrorDto(errorResponse);
        assertEquals("Name is required", errorDto.getMessage());

        bodyJson = testHelper.candidateRequestJson(CandidateRequest.builder().name("Allan").build());
        errorResponse = mockMvc.perform(post(CandidateApiTestHelper.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        errorDto = testHelper.jsonToResponseErrorDto(errorResponse);
        assertEquals("Email is required", errorDto.getMessage());

        bodyJson = testHelper.candidateRequestJson(CandidateRequest.builder().name("Allan").email("email").build());
        errorResponse = mockMvc.perform(post(CandidateApiTestHelper.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        errorDto = testHelper.jsonToResponseErrorDto(errorResponse);
        assertEquals("Email is invalid", errorDto.getMessage());
    }

    @Test
    void invalid_id_exception() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get(CandidateApiTestHelper.PATH_WITH_ID, new ObjectId().toString()))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        var bodyJson = testHelper.candidateRequestJson(CandidateRequest.builder().name("Allan Weber").email("mail@mail.com").build());
        mockMvc.perform(MockMvcRequestBuilders
                .put(CandidateApiTestHelper.PATH_WITH_ID, new ObjectId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isNotFound())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders
                .delete(CandidateApiTestHelper.PATH_WITH_ID, new ObjectId().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void exception_when_email_exists() throws Exception {
        //Create One
        var createDto = CandidateRequest
                .builder()
                .name("Allan")
                .email("mail@mail.com")
                .build();
        CandidateResponse candidateResponse = testHelper.create(createDto);

        var bodyJson = testHelper.candidateRequestJson(CandidateRequest
                .builder()
                .name("Allan Weber")
                .email("mail@mail.com")
                .build());
        var errorResponse = mockMvc.perform(post(CandidateApiTestHelper.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isConflict())
                .andReturn().getResponse().getContentAsString();

        ResponseErrorDto errorDto = testHelper.jsonToResponseErrorDto(errorResponse);
        assertEquals("Candidate email mail@mail.com already exist", errorDto.getMessage());

        testHelper.delete(candidateResponse.getId());
    }
}
