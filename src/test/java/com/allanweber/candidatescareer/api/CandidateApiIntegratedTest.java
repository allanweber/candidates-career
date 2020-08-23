package com.allanweber.candidatescareer.api;

import com.allanweber.candidatescareer.domain.candidate.dto.*;
import com.allanweber.candidatescareer.domain.candidate.mapper.CandidateMapper;
import com.allanweber.candidatescareer.domain.candidate.repository.Candidate;
import com.allanweber.candidatescareer.domain.candidate.repository.CandidateRepository;
import com.allanweber.candidatescareer.domain.helper.ObjectMapperHelper;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType.*;
import static com.allanweber.candidatescareer.domain.candidate.dto.SocialStatus.*;
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

    private final ObjectWriter requestWriter = ObjectMapperHelper.get().writerFor(CandidateRequest.class);
    private final ObjectReader responseReader = ObjectMapperHelper.get().readerFor(CandidateResponse.class);
    private final ObjectReader arrayResponseReader = ObjectMapperHelper.get().readerFor(new TypeReference<List<CandidateResponse>>() {
    });
    private final ObjectWriter arraySocialNetworkTypeWriter = ObjectMapperHelper.get().writerFor(new TypeReference<List<SocialNetworkType>>() {
    });
    private final ObjectReader responseErrorDtoReader = ObjectMapperHelper.get().readerFor(ResponseErrorDto.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CandidateRepository repository;

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
        var createDto = CandidateRequest
                .builder()
                .name("Allan")
                .email("mail@mail.com")
                .build();
        var created1 = create(createDto);
        assertEquals(createDto.getName(), created1.getName());
        assertEquals(createDto.getEmail(), created1.getEmail());
        assertNull(created1.getSocialNetwork());
        assertNull(created1.getSocialNetwork());
        assertFalse(created1.getId().isEmpty());

        // Is 1
        all = getAll();
        assertFalse(all.isEmpty());
        assertEquals(created1.getName(), all.get(0).getName());

        var get = getOne(created1.getId());
        assertEquals(created1.getName(), get.getName());
        assertEquals(created1.getSocialNetwork(), get.getSocialNetwork());

        // Update
        var updateDto = CandidateRequest.builder().name("Allan Weber").email("allan@mail.com").build();
        update(created1.getId(), updateDto);

        // Is 1
        all = getAll();
        assertFalse(all.isEmpty());
        assertEquals(1, all.size());
        assertEquals("Allan Weber", all.get(0).getName());
        assertEquals("allan@mail.com", all.get(0).getEmail());

        var createDto2 = CandidateRequest.builder().name("Weber").email("weber@mail.com").build();
        var created2 = create(createDto2);
        assertEquals(createDto2.getName(), created2.getName());
        assertEquals(createDto2.getEmail(), created2.getEmail());
        assertFalse(created2.getId().isEmpty());

        // Is 2
        all = getAll();
        assertEquals(2, all.size());

        get = getOne(created2.getId());
        assertEquals(created2.getName(), get.getName());
        assertEquals(created2.getEmail(), get.getEmail());

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
    void test_update_not_override_properties() throws Exception {

        //Create One
        var createDto = CandidateRequest
                .builder()
                .name("Allan")
                .email("mail@mail.com")
                .build();
        var created = create(createDto);
        assertEquals(createDto.getName(), created.getName());
        assertEquals(createDto.getEmail(), created.getEmail());
        assertNull(created.getSocialNetwork());
        assertNull(created.getSocialNetwork());
        assertFalse(created.getId().isEmpty());

        //Update via Repository
        Candidate candidateEntity = CandidateMapper.toEntity(createDto)
                .withId(created.getId())
                .withSocialEntries(Arrays.asList(
                        SocialEntry.builder().type(SocialNetworkType.GITHUB).status(GRANTED).build(),
                        SocialEntry.builder().type(SocialNetworkType.LINKEDIN).status(PENDING).build()
                ))
                .withSocialNetwork(Arrays.asList(
                        SocialNetworkDto.builder().type(TWITTER).url("https://twitter.com/acassianoweber").build(),
                        SocialNetworkDto.builder().type(SocialNetworkType.WEBSITE).url("http://meusite.com").build()
                ));
        repository.save(candidateEntity);

        //Check updates
        var all = getAll();
        assertEquals(1, getAll().size());
        assertEquals(created.getId(), all.get(0).getId());
        assertEquals(created.getName(), all.get(0).getName());
        assertEquals(created.getEmail(), all.get(0).getEmail());
        assertEquals(2, all.get(0).getSocialNetwork().size());
        assertEquals(2, all.get(0).getSocialEntries().size());

        //Update Name and Email
        var updateDto = CandidateRequest.builder().name("Allan Weber").email("allan@mail.com").build();
        CandidateResponse updated = update(created.getId(), updateDto);

        //Check updates1
        all = getAll();
        assertEquals(1, getAll().size());
        assertEquals(updated.getId(), all.get(0).getId());
        assertEquals(updated.getName(), all.get(0).getName());
        assertEquals(updated.getEmail(), all.get(0).getEmail());
        assertEquals(2, all.get(0).getSocialNetwork().size());
        assertEquals(2, all.get(0).getSocialEntries().size());

        delete(updated.getId());
    }

    @Test
    void invalid_body_exception() throws Exception {
        var bodyJson = requestWriter.writeValueAsString(CandidateRequest.builder().email("email@mail.com").build());
        var errorResponse = mockMvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        ResponseErrorDto errorDto = responseErrorDtoReader.readValue(errorResponse);
        assertEquals("Name is required", errorDto.getMessage());

        bodyJson = requestWriter.writeValueAsString(CandidateRequest.builder().name("Allan").build());
        errorResponse = mockMvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        errorDto = responseErrorDtoReader.readValue(errorResponse);
        assertEquals("Email is required", errorDto.getMessage());

        bodyJson = requestWriter.writeValueAsString(CandidateRequest.builder().name("Allan").email("email").build());
        errorResponse = mockMvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        errorDto = responseErrorDtoReader.readValue(errorResponse);
        assertEquals("Email is invalid", errorDto.getMessage());
    }

    @Test
    void invalid_id_exception() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get(PATH_WITH_ID, new ObjectId().toString()))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        var bodyJson = requestWriter.writeValueAsString(CandidateRequest.builder().name("Allan Weber").email("mail@mail.com").build());
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

    @Test
    void exception_when_email_exists() throws Exception {
        //Create One
        var createDto = CandidateRequest
                .builder()
                .name("Allan")
                .email("mail@mail.com")
                .build();
        CandidateResponse candidateResponse = create(createDto);

        var bodyJson = requestWriter.writeValueAsString(CandidateRequest
                .builder()
                .name("Allan Weber")
                .email("mail@mail.com")
                .build());
        var errorResponse = mockMvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isConflict())
                .andReturn().getResponse().getContentAsString();

        ResponseErrorDto errorDto = responseErrorDtoReader.readValue(errorResponse);
        assertEquals("Candidate email mail@mail.com already exist", errorDto.getMessage());

        delete(candidateResponse.getId());
    }

    @Test
    void add_and_replace_social_entries() throws Exception {
        var dto = CandidateRequest
                .builder()
                .name("Allan")
                .email("mail@mail.com")
                .build();
        CandidateResponse candidateResponse = create(dto);

        candidateResponse = addSocialEntry(candidateResponse.getId(), Collections.singletonList(TWITTER));
        assertEquals(1, candidateResponse.getSocialEntries().size());

        candidateResponse = addSocialEntry(candidateResponse.getId(), Collections.singletonList(GITHUB));
        assertEquals(2, candidateResponse.getSocialEntries().size());
        assertEquals(PENDING, candidateResponse.getSocialEntries().stream().filter(entry -> entry.getType().equals(TWITTER)).findFirst()
                .orElseThrow(NullPointerException::new).getStatus());
        assertEquals(PENDING, candidateResponse.getSocialEntries().stream().filter(entry -> entry.getType().equals(GITHUB)).findFirst()
                .orElseThrow(NullPointerException::new).getStatus());

        List<SocialEntry> socialEntries = Arrays.asList(
                SocialEntry.builder().type(TWITTER).status(GRANTED).build(),
                SocialEntry.builder().type(GITHUB).status(GRANTED).build(),
                SocialEntry.builder().type(LINKEDIN).status(DENIED).build()
        );
        Candidate candidate = repository.findById(candidateResponse.getId()).orElseThrow(NullPointerException::new);
        candidate.removeEqualEntries(socialEntries.stream().map(SocialEntry::getType).collect(Collectors.toList()));
        candidate.addSocialEntries(socialEntries);
        repository.save(candidate);
        candidateResponse = getOne(candidateResponse.getId());
        assertEquals(3, candidateResponse.getSocialEntries().size());
        assertEquals(GRANTED, candidateResponse.getSocialEntries().stream().filter(entry -> entry.getType().equals(TWITTER)).findFirst()
                .orElseThrow(NullPointerException::new).getStatus());
        assertEquals(GRANTED, candidateResponse.getSocialEntries().stream().filter(entry -> entry.getType().equals(GITHUB)).findFirst()
                .orElseThrow(NullPointerException::new).getStatus());
        assertEquals(DENIED, candidateResponse.getSocialEntries().stream().filter(entry -> entry.getType().equals(LINKEDIN)).findFirst()
                .orElseThrow(NullPointerException::new).getStatus());

        candidateResponse = addSocialEntry(candidateResponse.getId(), socialEntries.stream().map(SocialEntry::getType).collect(Collectors.toList()));
        assertEquals(3, candidateResponse.getSocialEntries().size());
        assertEquals(PENDING, candidateResponse.getSocialEntries().stream().filter(entry -> entry.getType().equals(TWITTER)).findFirst()
                .orElseThrow(NullPointerException::new).getStatus());
        assertEquals(PENDING, candidateResponse.getSocialEntries().stream().filter(entry -> entry.getType().equals(GITHUB)).findFirst()
                .orElseThrow(NullPointerException::new).getStatus());
        assertEquals(PENDING, candidateResponse.getSocialEntries().stream().filter(entry -> entry.getType().equals(LINKEDIN)).findFirst()
                .orElseThrow(NullPointerException::new).getStatus());


        delete(candidateResponse.getId());
    }

    private List<CandidateResponse> getAll() throws Exception {
        var getAllResponse = mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        return arrayResponseReader.readValue(getAllResponse);
    }

    private CandidateResponse create(CandidateRequest body) throws Exception {
        var bodyJson = requestWriter.writeValueAsString(body);
        var postResponse = mockMvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return responseReader.readValue(postResponse);
    }

    private CandidateResponse getOne(String id) throws Exception {
        var getResponse = mockMvc.perform(MockMvcRequestBuilders
                .get(PATH_WITH_ID, id))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return responseReader.readValue(getResponse);
    }

    private CandidateResponse update(String id, CandidateRequest body) throws Exception {
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

    private CandidateResponse addSocialEntry(String id, List<SocialNetworkType> entries) throws Exception {
        var bodyJson = arraySocialNetworkTypeWriter.writeValueAsString(entries);
        var putResponse = mockMvc.perform(MockMvcRequestBuilders
                .put(PATH_WITH_ID.concat("/social-entry"), id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return responseReader.readValue(putResponse);
    }
}
