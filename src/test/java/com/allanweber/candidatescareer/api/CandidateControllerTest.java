package com.allanweber.candidatescareer.api;

import com.allanweber.candidatescareer.domain.candidate.CandidateService;
import com.allanweber.candidatescareer.domain.candidate.dto.CandidateRequest;
import com.allanweber.candidatescareer.domain.candidate.dto.CandidateResponse;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CandidateControllerTest {

    @Mock
    CandidateService service;

    @InjectMocks
    CandidateController controller;

    @Test
    void getAll() {
        when(service.getAll()).thenReturn(mockResponses());
        ResponseEntity<List<CandidateResponse>> responseEntity = controller.getAll();
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(2, Objects.requireNonNull(responseEntity.getBody()).size());
    }

    @Test
    void get() {
        CandidateResponse response = mockResponses().get(0);
        when(service.getById(response.getId())).thenReturn(response);
        ResponseEntity<CandidateResponse> responseEntity = controller.get(response.getId());
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(response, Objects.requireNonNull(responseEntity.getBody()));
    }

    @Test
    void create() {
        CandidateRequest request = CandidateRequest.builder().name("name").email("mail").build();
        CandidateResponse response = mockResponses().get(0);
        when(service.insert(request)).thenReturn(response);
        ResponseEntity<CandidateResponse> responseEntity = controller.create(request);
        assertEquals(201, responseEntity.getStatusCodeValue());
        assertTrue(responseEntity.getHeaders().containsKey("Location"));
        assertEquals("/candidates/" + response.getId(), Objects.requireNonNull(responseEntity.getHeaders().get("Location")).get(0));
        assertEquals(response, Objects.requireNonNull(responseEntity.getBody()));
    }

    @Test
    void update() {
        String id = UUID.randomUUID().toString();
        CandidateRequest request = CandidateRequest.builder().name("name").email("mail").build();
        CandidateResponse response = mockResponses().get(0);
        when(service.update(id, request)).thenReturn(response);
        ResponseEntity<CandidateResponse> responseEntity = controller.update(id, request);
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(response, Objects.requireNonNull(responseEntity.getBody()));
    }

    @Test
    void delete() {
        String id = UUID.randomUUID().toString();
        doNothing().when(service).delete(id);
        ResponseEntity<?> responseEntity = controller.delete(id);
        assertEquals(410, responseEntity.getStatusCodeValue());
    }

    @Test
    void addSocialEntry() {
        String id = UUID.randomUUID().toString();
        CandidateResponse response = mockResponses().get(0);
        List<SocialNetworkType> socialNetworkTypes = Collections.singletonList(SocialNetworkType.LINKEDIN);
        when(service.addSocialEntries(id, socialNetworkTypes)).thenReturn(response);
        ResponseEntity<CandidateResponse> responseEntity = controller.addSocialEntry(id, socialNetworkTypes);
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(response, Objects.requireNonNull(responseEntity.getBody()));
    }

    @Test
    void image() {
        String id = UUID.randomUUID().toString();
        String imageBase64 = Base64.getEncoder().encodeToString("text".getBytes());
        when(service.getImage(id)).thenReturn(imageBase64);
        ResponseEntity<String> responseEntity = controller.image(id);
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(imageBase64, Objects.requireNonNull(responseEntity.getBody()));
    }

    List<CandidateResponse> mockResponses() {
        return Arrays.asList(
                CandidateResponse.builder().id(UUID.randomUUID().toString())
                        .name("candidate1")
                        .email("mail@mail.com")
                        .build(),
                CandidateResponse.builder().id(UUID.randomUUID().toString())
                        .name("candidate2")
                        .email("mail@mail.com")
                        .build()
        );
    }
}