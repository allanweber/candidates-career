package com.allanweber.candidatescareer.app.candidate;

import com.allanweber.candidatescareer.app.candidate.dto.SocialEntry;
import com.allanweber.candidatescareer.app.candidate.dto.SocialNetworkType;
import com.allanweber.candidatescareer.app.candidate.repository.Candidate;
import com.allanweber.candidatescareer.app.candidate.repository.CandidateMongoRepository;
import com.allanweber.candidatescareer.app.candidate.service.CandidateAnonymousService;
import com.allanweber.candidatescareer.app.social.linkedin.dto.LinkedInProfile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CandidateAnonymousServiceTest {

    @Mock
    CandidateMongoRepository candidateMongoRepository;

    @InjectMocks
    CandidateAnonymousService service;

    @Test
    void getSocialEntry() {
        Candidate entity = mockEntities().get(0);
        entity = entity.addSocialEntriesPending(Arrays.asList(SocialNetworkType.LINKEDIN, SocialNetworkType.TWITTER));
        when(candidateMongoRepository.findById(entity.getId())).thenReturn(Optional.of(entity));
        SocialEntry socialEntry = service.getSocialEntry(entity.getId(), SocialNetworkType.TWITTER);
        assertEquals(SocialNetworkType.TWITTER, socialEntry.getType());
    }

    @Test
    void getSocialEntry_notFound() {
        when(candidateMongoRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(HttpClientErrorException.class, () -> service.getSocialEntry("", SocialNetworkType.TWITTER));
    }

    @Test
    void saveLinkedInData() {
        Candidate entity = mockEntities().get(0);
        entity = entity.addSocialEntriesPending(Arrays.asList(SocialNetworkType.LINKEDIN, SocialNetworkType.TWITTER));
        when(candidateMongoRepository.findById(entity.getId())).thenReturn(Optional.of(entity));
        LinkedInProfile linkedInProfile = new LinkedInProfile("fist", "last", "image");
        Candidate candidateWithLinkedInData = entity.addLinkedInData(linkedInProfile);
        when(candidateMongoRepository.save(eq(candidateWithLinkedInData))).thenReturn(candidateWithLinkedInData);
        service.saveLinkedInData(entity.getId(), linkedInProfile);
    }

    @Test
    void saveLinkedInData_notFound() {
        when(candidateMongoRepository.findById(anyString())).thenReturn(Optional.empty());
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