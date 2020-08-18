package com.allanweber.candidatescareer.api;

import com.allanweber.candidatescareer.domain.candidate.dto.CandidateDto;
import com.allanweber.candidatescareer.domain.helper.ObjectMapperHelper;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("inttest")
class CandidateApiIntegratedTest {

    private static final String PATH = "/candidates";
    private static final String PATH_WITH_ID = String.format("%s/{candidateId}", PATH);

    private final ObjectWriter requestWriter = ObjectMapperHelper.get().writerFor(CandidateDto.class);
    private final ObjectReader responseReader = ObjectMapperHelper.get().readerFor(CandidateDto.class);
    private final ObjectReader arrayResponseReader = ObjectMapperHelper.get().readerFor(new TypeReference<List<CandidateDto>>() {
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
        var createDto = CandidateDto.builder().name("Allan").gitHubProfile("https://github.com/allanweber").build();
        var created1 = create(createDto);
        assertEquals(createDto.getName(), created1.getName());
        assertEquals("https://github.com/allanweber", created1.getGitHubProfile());
        assertFalse(created1.getId().isEmpty());

        // Is 1
        all = getAll();
        assertFalse(all.isEmpty());
        assertEquals(created1, all.get(0));

        var get = getOne(created1.getId());
        assertEquals(created1, get);

        // Update
        var updateDto = CandidateDto.builder().name("Allan Weber").gitHubProfile("https://github.com/allanweber").build();
        update(created1.getId(), updateDto);

        // Is 1
        all = getAll();
        assertFalse(all.isEmpty());
        assertEquals(1, all.size());
        assertEquals("Allan Weber", all.get(0).getName());
        assertEquals("https://github.com/allanweber", all.get(0).getGitHubProfile());

        var createDto2 = CandidateDto.builder().name("Weber").gitHubProfile("https://github.com/weber").build();
        var created2 = create(createDto2);
        assertEquals(createDto2.getName(), created2.getName());
        assertEquals(createDto2.getGitHubProfile(), created2.getGitHubProfile());
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
    void gitUrl_is_valid_null() throws Exception {
        // URL should be valid null
        var bodyJson = requestWriter.writeValueAsString(CandidateDto.builder().name("Allan").build());
        var responseJson = mockMvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        CandidateDto dto = responseReader.readValue(responseJson);
        assertNull(dto.getGitHubProfile());
        delete(dto.getId());
    }

    @Test
    void invalid_body_exception() throws Exception {
        var bodyJson = requestWriter.writeValueAsString(CandidateDto.builder().build());
        var errorResponse = mockMvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        ResponseErrorDto errorDto = responseErrorDtoReader.readValue(errorResponse);
        assertEquals("Name is required", errorDto.getMessage());

        bodyJson = requestWriter.writeValueAsString(CandidateDto.builder().name("Allan").gitHubProfile("git").build());
        errorResponse = mockMvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        errorDto = responseErrorDtoReader.readValue(errorResponse);
        assertEquals("Git hub url is invalid", errorDto.getMessage());
    }

    @Test
    void invalid_id_exception() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get(PATH_WITH_ID, new ObjectId().toString()))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        var bodyJson = requestWriter.writeValueAsString(CandidateDto.builder().name("Allan Weber").gitHubProfile("https://github.com/allanweber").build());
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

    private List<CandidateDto> getAll() throws Exception {
        var getAllResponse = mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        return arrayResponseReader.readValue(getAllResponse);
    }

    private CandidateDto create(CandidateDto body) throws Exception {
        var bodyJson = requestWriter.writeValueAsString(body);
        var postResponse = mockMvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return responseReader.readValue(postResponse);
    }

    private CandidateDto getOne(String id) throws Exception {
        var getResponse = mockMvc.perform(MockMvcRequestBuilders
                .get(PATH_WITH_ID, id))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return responseReader.readValue(getResponse);
    }

    private CandidateDto update(String id, CandidateDto body) throws Exception {
        var bodyJson = requestWriter.writeValueAsString(body);
        var putResponse = mockMvc.perform(MockMvcRequestBuilders
                .put(PATH_WITH_ID, id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return responseReader.readValue(putResponse);
    }

    private void delete(String id) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete(PATH_WITH_ID, id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isGone())
                .andReturn();
    }
}