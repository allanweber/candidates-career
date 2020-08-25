package com.allanweber.candidatescareer.api.helpers;

import com.allanweber.candidatescareer.domain.candidate.dto.CandidateRequest;
import com.allanweber.candidatescareer.domain.candidate.dto.CandidateResponse;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType;
import com.allanweber.candidatescareer.domain.candidate.repository.CandidateRepository;
import com.allanweber.candidatescareer.domain.helper.ObjectMapperHelper;
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

public class CandidateApiTestHelper {

    public static final String PATH = "/candidates";
    public static final String PATH_WITH_ID = String.format("%s/{candidateId}", PATH);

    private final ObjectWriter requestWriter = ObjectMapperHelper.get().writerFor(CandidateRequest.class);
    private final ObjectReader responseReader = ObjectMapperHelper.get().readerFor(CandidateResponse.class);
    private final ObjectReader arrayResponseReader = ObjectMapperHelper.get().readerFor(new TypeReference<List<CandidateResponse>>() {
    });
    private final ObjectWriter arraySocialNetworkTypeWriter = ObjectMapperHelper.get().writerFor(new TypeReference<List<SocialNetworkType>>() {
    });
    private final ObjectReader responseErrorDtoReader = ObjectMapperHelper.get().readerFor(ResponseErrorDto.class);

    private final CandidateRepository repository;
    private final MockMvc mockMvc;

    public CandidateApiTestHelper(CandidateRepository repository, MockMvc mockMvc) {
        this.repository = repository;
        this.mockMvc = mockMvc;
    }

    public void deleteAll() {
        repository.findAll().forEach(repository::delete);
    }

    public List<CandidateResponse> getAll() throws Exception {
        var getAllResponse = mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        return arrayResponseReader.readValue(getAllResponse);
    }

    public CandidateResponse create(CandidateRequest body) throws Exception {
        var bodyJson = requestWriter.writeValueAsString(body);
        var postResponse = mockMvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return responseReader.readValue(postResponse);
    }

    public CandidateResponse getOne(String id) throws Exception {
        var getResponse = mockMvc.perform(MockMvcRequestBuilders
                .get(PATH_WITH_ID, id))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return responseReader.readValue(getResponse);
    }

    public CandidateResponse update(String id, CandidateRequest body) throws Exception {
        var bodyJson = requestWriter.writeValueAsString(body);
        var putResponse = mockMvc.perform(MockMvcRequestBuilders
                .put(PATH_WITH_ID, id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return responseReader.readValue(putResponse);
    }

    public void delete(String id) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete(PATH_WITH_ID, id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isGone())
                .andReturn();
    }

    public CandidateResponse addSocialEntry(String id, List<SocialNetworkType> entries) throws Exception {
        var bodyJson = arraySocialNetworkTypeWriter.writeValueAsString(entries);
        var putResponse = mockMvc.perform(MockMvcRequestBuilders
                .put(PATH_WITH_ID.concat("/social-entry"), id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return responseReader.readValue(putResponse);
    }

    public String candidateRequestJson(CandidateRequest dto) throws JsonProcessingException {
        return requestWriter.writeValueAsString(dto);
    }

    public ResponseErrorDto jsonToResponseErrorDto(String json) throws JsonProcessingException {
        return responseErrorDtoReader.readValue(json);
    }
}
