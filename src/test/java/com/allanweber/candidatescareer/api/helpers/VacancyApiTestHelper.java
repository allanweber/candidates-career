package com.allanweber.candidatescareer.api.helpers;

import com.allanweber.candidatescareer.domain.helper.ObjectMapperHelper;
import com.allanweber.candidatescareer.domain.vacancy.dto.VacancyDto;
import com.allanweber.candidatescareer.domain.vacancy.repository.VacancyRepository;
import com.allanweber.candidatescareer.infrastructure.handler.dto.ResponseErrorDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class VacancyApiTestHelper {

    public static final String PATH = "/vacancies";
    public static final String PATH_WITH_ID = String.format("%s/{vacancyId}", PATH);
    
    private final ObjectWriter requestWriter = ObjectMapperHelper.get().writerFor(VacancyDto.class);
    private final ObjectReader responseReader = ObjectMapperHelper.get().readerFor(VacancyDto.class);
    private final ObjectReader arrayResponseReader = ObjectMapperHelper.get().readerFor(new TypeReference<List<VacancyDto>>() {});
    private final ObjectReader responseErrorDtoReader = ObjectMapperHelper.get().readerFor(ResponseErrorDto.class);

    private final VacancyRepository repository;
    private final MockMvc mockMvc;

    public VacancyApiTestHelper(VacancyRepository repository, MockMvc mockMvc) {
        this.repository = repository;
        this.mockMvc = mockMvc;
    }
    
    public void deleteAll() {
        repository.findAll().forEach(repository::delete);
    }

    public void delete(String id) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete(PATH_WITH_ID, id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isGone())
                .andReturn();
    }

    public VacancyDto update(String id, VacancyDto body) throws Exception {
        var bodyJson = requestWriter.writeValueAsString(body);
        var putResponse = mockMvc.perform(MockMvcRequestBuilders
                .put(PATH_WITH_ID, id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return responseReader.readValue(putResponse);
    }

    public VacancyDto getOne(String id) throws Exception {
        var getResponse = mockMvc.perform(MockMvcRequestBuilders
                .get(PATH_WITH_ID, id))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return responseReader.readValue(getResponse);
    }

    public VacancyDto create(VacancyDto body) throws Exception {
        var bodyJson = requestWriter.writeValueAsString(body);
        var postResponse = mockMvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return responseReader.readValue(postResponse);
    }

    public List<VacancyDto> getAll() throws Exception {
        var getAllResponse = mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        return arrayResponseReader.readValue(getAllResponse);
    }

    public String vacancyDtoJson(VacancyDto dto) throws JsonProcessingException {
        return requestWriter.writeValueAsString(dto);
    }

    public ResponseErrorDto jsonToResponseErrorDto(String json) throws JsonProcessingException {
        return responseErrorDtoReader.readValue(json);
    }
}
