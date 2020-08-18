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

    private static final String PATH = "/vacancies";
    private static final String PATH_WITH_ID = String.format("%s/{vacancyId}", PATH);

    private final ObjectWriter requestWriter = ObjectMapperHelper.get().writerFor(VacancyDto.class);
    private final ObjectReader responseReader = ObjectMapperHelper.get().readerFor(VacancyDto.class);
    private final ObjectReader arrayResponseReader = ObjectMapperHelper.get().readerFor(new TypeReference<List<VacancyDto>>() {
    });
    private final ObjectReader responseErrorDtoReader = ObjectMapperHelper.get().readerFor(ResponseErrorDto.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VacancyRepository repository;

    @BeforeEach
    public void setUp() {
        repository.findAll().forEach(entity -> repository.delete(entity));
    }

    @Test
    void test_all_chain() throws Exception {

        // Is 0
        var all = getAll();
        assertTrue(all.isEmpty());

        //Create first
        var createDto = VacancyDto.builder().name("Java").skills(Arrays.asList("Java", "Maven")).build();
        var created1 = create(createDto);
        assertEquals(createDto.getName(), created1.getName());
        assertEquals(createDto.getSkills(), created1.getSkills());
        assertFalse(created1.getId().isEmpty());

        // Is 1
        all = getAll();
        assertFalse(all.isEmpty());
        assertEquals(created1, all.get(0));

        var get = getOne(created1.getId());
        assertEquals(created1, get);

        // Update
        var updateDto = VacancyDto.builder().name("Java Senior").skills(Arrays.asList("Java", "Maven", "Senior")).build();
        update(created1.getId(), updateDto);

        // Is 1
        all = getAll();
        assertFalse(all.isEmpty());
        assertEquals(1, all.size());
        assertEquals("Java Senior", all.get(0).getName());
        assertEquals(3, all.get(0).getSkills().size());

        var createDto2 = VacancyDto.builder().name(".NET").skills(Arrays.asList("C#", "Entity")).build();
        var created2 = create(createDto2);
        assertEquals(createDto2.getName(), created2.getName());
        assertEquals(createDto2.getSkills(), created2.getSkills());
        assertFalse(created2.getId().isEmpty());

        // Is 2
        all = getAll();
        assertEquals(2, all.size());

        get = getOne(created2.getId());
        assertEquals(created2, get);

        delete(created1.getId());

        // Is 1
        all = getAll();
        assertEquals(1, all.size());

        delete(created2.getId());

        // Is 0
        all = getAll();
        assertTrue(all.isEmpty());
    }

    @Test
    void invalid_body_exception() throws Exception {
        var bodyJson = requestWriter.writeValueAsString(VacancyDto.builder().name("Java").build());
        var errorResponse = mockMvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        ResponseErrorDto errorDto = responseErrorDtoReader.readValue(errorResponse);
        assertEquals("Skills are required", errorDto.getMessage());

        bodyJson = requestWriter.writeValueAsString(VacancyDto.builder().skills(Arrays.asList("C#", "Entity")).build());
        errorResponse = mockMvc.perform(post(PATH)
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
                .get(PATH_WITH_ID, new ObjectId().toString()))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        var bodyJson = requestWriter.writeValueAsString(VacancyDto.builder().name("Java").skills(Arrays.asList("Java", "Maven")).build());
        mockMvc.perform(MockMvcRequestBuilders
                .put(PATH_WITH_ID, new ObjectId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isNotFound())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders
                .delete(PATH_WITH_ID, new ObjectId().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    private void delete(String id) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete(PATH_WITH_ID, id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isGone())
                .andReturn();
    }

    private VacancyDto update(String id, VacancyDto body) throws Exception {
        var bodyJson = requestWriter.writeValueAsString(body);
        var putResponse = mockMvc.perform(MockMvcRequestBuilders
                .put(PATH_WITH_ID, id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return responseReader.readValue(putResponse);
    }

    private VacancyDto getOne(String id) throws Exception {
        var getResponse = mockMvc.perform(MockMvcRequestBuilders
                .get(PATH_WITH_ID, id))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return responseReader.readValue(getResponse);
    }

    private VacancyDto create(VacancyDto body) throws Exception {
        var bodyJson = requestWriter.writeValueAsString(body);
        var postResponse = mockMvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return responseReader.readValue(postResponse);
    }

    private List<VacancyDto> getAll() throws Exception {
        var getAllResponse = mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        return arrayResponseReader.readValue(getAllResponse);
    }
}
