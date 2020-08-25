package com.allanweber.candidatescareer.api;

import com.allanweber.candidatescareer.api.helpers.VacancyApiTestHelper;
import com.allanweber.candidatescareer.domain.vacancy.dto.VacancyDto;
import com.allanweber.candidatescareer.domain.vacancy.repository.VacancyRepository;
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

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("inttest")
public class VacancyApiIntegratedExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VacancyRepository repository;

    private VacancyApiTestHelper testHelper;

    @BeforeEach
    public void setUp() {
        this.testHelper = new VacancyApiTestHelper(repository, mockMvc);
        this.testHelper.deleteAll();
    }

    @Test
    void invalid_body_exception() throws Exception {
        var bodyJson = testHelper.vacancyDtoJson(VacancyDto.builder().name("Java").build());
        var errorResponse = mockMvc.perform(post(VacancyApiTestHelper.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        ResponseErrorDto errorDto = testHelper.jsonToResponseErrorDto(errorResponse);
        assertEquals("Skills are required", errorDto.getMessage());

        bodyJson = testHelper.vacancyDtoJson(VacancyDto.builder().skills(Arrays.asList("C#", "Entity")).build());
        errorResponse = mockMvc.perform(post(VacancyApiTestHelper.PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        errorDto = testHelper.jsonToResponseErrorDto(errorResponse);
        assertEquals("Name is required", errorDto.getMessage());
    }

    @Test
    void invalid_id_exception() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get(VacancyApiTestHelper.PATH_WITH_ID, new ObjectId().toString()))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        var bodyJson = testHelper.vacancyDtoJson(VacancyDto.builder().name("Java").skills(Arrays.asList("Java", "Maven")).build());
        mockMvc.perform(MockMvcRequestBuilders
                .put(VacancyApiTestHelper.PATH_WITH_ID, new ObjectId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isNotFound())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders
                .delete(VacancyApiTestHelper.PATH_WITH_ID, new ObjectId().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }


}
