package com.allanweber.candidatescareer.api;

import com.allanweber.candidatescareer.api.helpers.CandidateApiTestHelper;
import com.allanweber.candidatescareer.domain.candidate.dto.CandidateRequest;
import com.allanweber.candidatescareer.domain.candidate.dto.CandidateResponse;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialEntry;
import com.allanweber.candidatescareer.domain.candidate.repository.Candidate;
import com.allanweber.candidatescareer.domain.candidate.repository.CandidateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType.*;
import static com.allanweber.candidatescareer.domain.candidate.dto.SocialStatus.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("inttest")
class CandidateApiIntegratedTest {

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
    void test_happy_path() throws Exception {

        // Is 0
        var all = testHelper.getAll();
        assertTrue(all.isEmpty());

        //Create first
        var createDto = CandidateRequest
                .builder()
                .name("Allan")
                .email("mail@mail.com")
                .build();
        var created1 = testHelper.create(createDto);
        assertEquals(createDto.getName(), created1.getName());
        assertEquals(createDto.getEmail(), created1.getEmail());
        assertNull(created1.getSocialNetwork());
        assertNull(created1.getSocialNetwork());
        assertFalse(created1.getId().isEmpty());

        // Is 1
        all = testHelper.getAll();
        assertFalse(all.isEmpty());
        assertEquals(created1.getName(), all.get(0).getName());

        var get = testHelper.getOne(created1.getId());
        assertEquals(created1.getName(), get.getName());
        assertEquals(created1.getSocialNetwork(), get.getSocialNetwork());

        // Update
        var updateDto = CandidateRequest.builder().name("Allan Weber").email("allan@mail.com").build();
        testHelper.update(created1.getId(), updateDto);

        // Is 1
        all = testHelper.getAll();
        assertFalse(all.isEmpty());
        assertEquals(1, all.size());
        assertEquals("Allan Weber", all.get(0).getName());
        assertEquals("allan@mail.com", all.get(0).getEmail());

        var createDto2 = CandidateRequest.builder().name("Weber").email("weber@mail.com").build();
        var created2 = testHelper.create(createDto2);
        assertEquals(createDto2.getName(), created2.getName());
        assertEquals(createDto2.getEmail(), created2.getEmail());
        assertFalse(created2.getId().isEmpty());

        // Is 2
        all = testHelper.getAll();
        assertEquals(2, all.size());

        get = testHelper.getOne(created2.getId());
        assertEquals(created2.getName(), get.getName());
        assertEquals(created2.getEmail(), get.getEmail());

        testHelper.delete(created1.getId());

        // Is 1
        all = testHelper.getAll();
        assertEquals(1, all.size());

        testHelper.delete(created2.getId());

        // Is 0
        all = testHelper.getAll();
        assertTrue(all.isEmpty());
    }


    @Test
    void add_and_replace_social_entries() throws Exception {
        var dto = CandidateRequest
                .builder()
                .name("Allan")
                .email("mail@mail.com")
                .build();
        CandidateResponse candidateResponse = testHelper.create(dto);

        candidateResponse = testHelper.addSocialEntry(candidateResponse.getId(), Collections.singletonList(TWITTER));
        assertEquals(1, candidateResponse.getSocialEntries().size());

        candidateResponse = testHelper.addSocialEntry(candidateResponse.getId(), Collections.singletonList(GITHUB));
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
        Candidate candidateNewEntries = candidate.addSocialEntries(socialEntries);
        repository.save(candidateNewEntries);
        candidateResponse = testHelper.getOne(candidateResponse.getId());
        assertEquals(3, candidateResponse.getSocialEntries().size());
        assertEquals(GRANTED, candidateResponse.getSocialEntries().stream().filter(entry -> entry.getType().equals(TWITTER)).findFirst()
                .orElseThrow(NullPointerException::new).getStatus());
        assertEquals(GRANTED, candidateResponse.getSocialEntries().stream().filter(entry -> entry.getType().equals(GITHUB)).findFirst()
                .orElseThrow(NullPointerException::new).getStatus());
        assertEquals(DENIED, candidateResponse.getSocialEntries().stream().filter(entry -> entry.getType().equals(LINKEDIN)).findFirst()
                .orElseThrow(NullPointerException::new).getStatus());

        candidateResponse = testHelper.addSocialEntry(candidateResponse.getId(), socialEntries.stream().map(SocialEntry::getType).collect(Collectors.toList()));
        assertEquals(3, candidateResponse.getSocialEntries().size());
        assertEquals(PENDING, candidateResponse.getSocialEntries().stream().filter(entry -> entry.getType().equals(TWITTER)).findFirst()
                .orElseThrow(NullPointerException::new).getStatus());
        assertEquals(PENDING, candidateResponse.getSocialEntries().stream().filter(entry -> entry.getType().equals(GITHUB)).findFirst()
                .orElseThrow(NullPointerException::new).getStatus());
        assertEquals(PENDING, candidateResponse.getSocialEntries().stream().filter(entry -> entry.getType().equals(LINKEDIN)).findFirst()
                .orElseThrow(NullPointerException::new).getStatus());


        testHelper.delete(candidateResponse.getId());
    }
}
