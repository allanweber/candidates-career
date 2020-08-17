package com.allanweber.candidatescareer.api;

import com.allanweber.candidatescareer.domain.helper.ObjectMapperHelper;
import com.allanweber.candidatescareer.domain.vacancy.dto.VacancyDto;
import com.allanweber.candidatescareer.domain.vacancy.repository.VacancyRepository;
import com.allanweber.candidatescareer.infrastructure.handler.dto.ResponseErrorDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("inttest")
class VacancyApiIntegratedTest {

    private static final String VACANCIES_PATH = "/vacancies";
    private static final String VACANCIES_PATH_WITH_ID = String.format("%s/{vacancyId}", VACANCIES_PATH);

    private final ObjectWriter vacancyRequestWriter = ObjectMapperHelper.get().writerFor(VacancyDto.class);
    private final ObjectReader vacancyResponseReader = ObjectMapperHelper.get().readerFor(VacancyDto.class);
    private final ObjectReader vacancyResponseReaderArray = ObjectMapperHelper.get().readerFor(new TypeReference<List<VacancyDto>>() {
    });
    private final ObjectReader responseErrorDtoReader = ObjectMapperHelper.get().readerFor(ResponseErrorDto.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VacancyRepository vacancyRepository;

    @BeforeEach
    public void setUp() {
        vacancyRepository.findAll().forEach(vacancy -> vacancyRepository.delete(vacancy));
    }

    @Test
    void test_all_chain() throws Exception {

        // Is 0
        List<VacancyDto> vacancies = getAll();
        String getAllResponse;
        assertTrue(vacancies.isEmpty());

        //Create first
        VacancyDto createDto = VacancyDto.builder().name("Java").skills(Arrays.asList("Java", "Maven")).build();
        VacancyDto created1 = create(createDto);
        assertEquals(createDto.getName(), created1.getName());
        assertEquals(createDto.getSkills(), created1.getSkills());
        assertFalse(created1.getId().isEmpty());

        // Is 1
        vacancies = getAll();
        assertFalse(vacancies.isEmpty());
        assertEquals(created1, vacancies.get(0));

        VacancyDto get = getOne(created1.getId());
        assertEquals(created1, get);

        // Update
        VacancyDto updateDto = VacancyDto.builder().name("Java Senior").skills(Arrays.asList("Java", "Maven", "Senior")).build();
        VacancyDto updatedVacancy = update(created1.getId(), updateDto);

        // Is 1
        vacancies = getAll();
        assertFalse(vacancies.isEmpty());
        assertEquals(1, vacancies.size());
        assertEquals("Java Senior", vacancies.get(0).getName());
        assertEquals(3, vacancies.get(0).getSkills().size());

        VacancyDto createDto2 = VacancyDto.builder().name(".NET").skills(Arrays.asList("C#", "Entity")).build();
        VacancyDto created2 = create(createDto2);
        assertEquals(createDto2.getName(), created2.getName());
        assertEquals(createDto2.getSkills(), created2.getSkills());
        assertFalse(created2.getId().isEmpty());

        // Is 2
        vacancies = getAll();
        assertEquals(2, vacancies.size());

        get = getOne(created2.getId());
        assertEquals(created2, get);

        delete(created1.getId());

        // Is 1
        vacancies = getAll();
        assertEquals(1, vacancies.size());

        delete(created2.getId());

        // Is 0
        vacancies = getAll();
        assertTrue(vacancies.isEmpty());
    }

    @Test
    void invalid_body_exception() throws Exception {
        String bodyJson = vacancyRequestWriter.writeValueAsString(VacancyDto.builder().name("Java").build());
        String errorResponse = mockMvc.perform(post(VACANCIES_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        ResponseErrorDto errorDto = responseErrorDtoReader.readValue(errorResponse);
        assertEquals("Skills are required", errorDto.getMessage());

        bodyJson = vacancyRequestWriter.writeValueAsString(VacancyDto.builder().skills(Arrays.asList("C#", "Entity")).build());
        errorResponse = mockMvc.perform(post(VACANCIES_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        errorDto = responseErrorDtoReader.readValue(errorResponse);
        assertEquals("Name is required", errorDto.getMessage());
    }

    @Test
    void invalid_id_exception() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get(VACANCIES_PATH_WITH_ID, new ObjectId().toString()))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        String bodyJson = vacancyRequestWriter.writeValueAsString(VacancyDto.builder().name("Java").skills(Arrays.asList("Java", "Maven")).build());
        mockMvc.perform(MockMvcRequestBuilders
                .put(VACANCIES_PATH_WITH_ID, new ObjectId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isNotFound())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders
                .delete(VACANCIES_PATH_WITH_ID, new ObjectId().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    private void delete(String id) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete(VACANCIES_PATH_WITH_ID, id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isGone())
                .andReturn();
    }

    private VacancyDto update(String id, VacancyDto body) throws Exception {
        String bodyJson = vacancyRequestWriter.writeValueAsString(body);
        String putResponse = mockMvc.perform(MockMvcRequestBuilders
                .put(VACANCIES_PATH_WITH_ID, id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return vacancyResponseReader.readValue(putResponse);
    }

    private VacancyDto getOne(String id) throws Exception {
        String getResponse = mockMvc.perform(MockMvcRequestBuilders
                .get(VACANCIES_PATH_WITH_ID, id))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return vacancyResponseReader.readValue(getResponse);
    }

    private VacancyDto create(VacancyDto body) throws Exception {
        String bodyJson = vacancyRequestWriter.writeValueAsString(body);
        String postResponse = mockMvc.perform(post(VACANCIES_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return vacancyResponseReader.readValue(postResponse);
    }

    private List<VacancyDto> getAll() throws Exception {
        String getAllResponse = mockMvc.perform(get(VACANCIES_PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        return vacancyResponseReaderArray.readValue(getAllResponse);
    }
}
