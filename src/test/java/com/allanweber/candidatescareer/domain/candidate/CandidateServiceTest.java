package com.allanweber.candidatescareer.domain.candidate;

import com.allanweber.candidatescareer.domain.candidate.dto.CandidateRequest;
import com.allanweber.candidatescareer.domain.candidate.dto.CandidateResponse;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialEntry;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType;
import com.allanweber.candidatescareer.domain.candidate.repository.Candidate;
import com.allanweber.candidatescareer.domain.candidate.repository.CandidateRepository;
import com.allanweber.candidatescareer.domain.linkedin.dto.LinkedInProfile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CandidateServiceTest {

    @Mock
    CandidateRepository repository;

    @InjectMocks
    CandidateService service;

    @Test
    void getAll() {
        when(repository.findAll()).thenReturn(mockEntities());
        List<CandidateResponse> dto = service.getAll();
        assertEquals(2, dto.size());
    }

    @Test
    void getById() {
        Candidate entity = mockEntities().get(0);
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
        CandidateResponse dto = service.getById(entity.getId());
        assertNotNull(dto);
    }

    @Test
    void getById_notFound() {
        when(repository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(HttpClientErrorException.class, () -> service.getById("id"));
    }

    @Test
    void update() {
        Candidate entity = mockEntities().get(0);
        CandidateRequest candidateRequest = CandidateRequest.builder().name("NET").email("mail@mail.com").build();
        entity = entity.withName("NET");
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(repository.save(eq(entity))).thenReturn(entity);
        CandidateResponse response = service.update(entity.getId(), candidateRequest);
        assertNotNull(response);
    }

    @Test
    void update_notFound() {
        CandidateRequest candidateRequest = CandidateRequest.builder().build();
        when(repository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(HttpClientErrorException.class, () -> service.update("", candidateRequest));
    }

    @Test
    void insert() {
        Candidate entity = mockEntities().get(0);
        CandidateRequest candidateRequest = CandidateRequest.builder().name("NET").build();
        when(repository.save(eq(entity))).thenReturn(entity);
        CandidateResponse response = service.insert(candidateRequest);
        assertNotNull(response);
    }

    @Test
    void delete() {
        Candidate entity = mockEntities().get(0);
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
        doNothing().when(repository).deleteById(entity.getId());
        service.delete(entity.getId());
    }

    @Test
    void delete_notFound() {
        when(repository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(HttpClientErrorException.class, () -> service.delete(""));
    }

    @Test
    void getImage() {
        Candidate entity = mockEntities().get(0);
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
        String image = service.getImage(entity.getId());
        assertNotNull(image);
    }

    @Test
    void getImage_notFound() {
        when(repository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(HttpClientErrorException.class, () -> service.getImage(""));
    }

    @Test
    void addSocialEntries() {
        Candidate entity = mockEntities().get(0);
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
        List<SocialNetworkType> socialNetworkTypes = Collections.singletonList(SocialNetworkType.LINKEDIN);
        entity = entity.addSocialEntriesPending(socialNetworkTypes);
        when(repository.save(eq(entity))).thenReturn(entity);
        CandidateResponse response = service.addSocialEntries(entity.getId(), socialNetworkTypes);
        assertNotNull(response);
    }

    @Test
    void addSocialEntries_notFound() {
        when(repository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(HttpClientErrorException.class, () -> service.addSocialEntries("", Collections.emptyList()));
    }


    @Test
    void getSocialEntry() {
        Candidate entity = mockEntities().get(0);
        entity = entity.addSocialEntriesPending(Arrays.asList(SocialNetworkType.LINKEDIN, SocialNetworkType.TWITTER));
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
        SocialEntry socialEntry = service.getSocialEntry(entity.getId(), SocialNetworkType.TWITTER);
        assertEquals(SocialNetworkType.TWITTER, socialEntry.getType());
    }

    @Test
    void getSocialEntry_notFound() {
        when(repository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(HttpClientErrorException.class, () -> service.getSocialEntry("", SocialNetworkType.TWITTER));
    }

    @Test
    void saveLinkedInData() {
        Candidate entity = mockEntities().get(0);
        entity = entity.addSocialEntriesPending(Arrays.asList(SocialNetworkType.LINKEDIN, SocialNetworkType.TWITTER));
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
        LinkedInProfile linkedInProfile = new LinkedInProfile("fist", "last", "image");
        Candidate candidateWithLinkedInData = entity.addLinkedInData(linkedInProfile);
        when(repository.save(eq(candidateWithLinkedInData))).thenReturn(candidateWithLinkedInData);
        service.saveLinkedInData(entity.getId(), linkedInProfile);
    }

    @Test
    void saveLinkedInData_notFound() {
        when(repository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(HttpClientErrorException.class, () -> service.saveLinkedInData("", null));
    }


    List<Candidate> mockEntities() {
        return Arrays.asList(
                Candidate.builder().id(UUID.randomUUID().toString())
                        .name("candidate1")
                        .email("mail@mail.com")
                        .image("any image base 64")
                        .build(),
                Candidate.builder().id(UUID.randomUUID().toString())
                        .name("candidate2")
                        .email("mail@mail.com")
                        .build()
        );
    }
}