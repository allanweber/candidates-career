package com.allanweber.candidatescareer.domain.candidate;

import com.allanweber.candidatescareer.domain.candidate.dto.*;
import com.allanweber.candidatescareer.domain.candidate.repository.Candidate;
import com.allanweber.candidatescareer.domain.candidate.repository.CandidateAuthenticatedRepository;
import com.allanweber.candidatescareer.domain.candidate.repository.CandidateMongoRepository;
import com.allanweber.candidatescareer.domain.social.linkedin.dto.LinkedInProfile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CandidateServiceTest {

    @Mock
    CandidateAuthenticatedRepository repository;

    @Mock
    CandidateSocialEmailService candidateSocialEmailService;

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
        CandidateUpdate dto = CandidateUpdate.builder().name("NET").email("mail@mail.com").build();
        entity = entity.withName("NET");
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(repository.save(eq(entity))).thenReturn(entity);
        CandidateResponse response = service.update(entity.getId(), dto);
        assertNotNull(response);
    }

    @Test
    void update_notFound() {
        CandidateUpdate candidateRequest = CandidateUpdate.builder().build();
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
        doNothing().when(candidateSocialEmailService).sendSocialAccess(any(), any());
        List<SocialEntry> entries = service.addSocialEntries(entity.getId(), socialNetworkTypes);
        assertNotNull(entries);
    }

    @Test
    void addSocialEntries_notFound() {
        when(repository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(HttpClientErrorException.class, () -> service.addSocialEntries("", Collections.emptyList()));
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