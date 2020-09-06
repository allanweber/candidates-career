package com.allanweber.candidatescareer.domain.candidate.repository;

import com.allanweber.candidatescareer.domain.candidate.dto.SocialEntry;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialStatus;
import com.allanweber.candidatescareer.domain.social.github.dto.GitHubProfile;
import com.allanweber.candidatescareer.domain.social.linkedin.dto.LinkedInProfile;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CandidateTest {

    @Test
    void test_addSocialEntriesPending() {
        Candidate candidate = Candidate.builder()
                .name("allan")
                .socialEntries(Arrays.asList(
                        SocialEntry.builder().status(SocialStatus.GRANTED).type(SocialNetworkType.LINKEDIN).build(),
                        SocialEntry.builder().status(SocialStatus.DENIED).type(SocialNetworkType.GITHUB).build()
                ))
                .build();

        List<SocialNetworkType> socialNetworkTypes = Arrays.asList(SocialNetworkType.TWITTER, SocialNetworkType.FACEBOOK);
        Candidate added = candidate.addSocialEntriesPending(socialNetworkTypes);
        assertEquals(4, added.getSocialEntries().size());
        assertEquals(SocialStatus.GRANTED, getSocialStatus(added.getSocialEntries(), SocialNetworkType.LINKEDIN));
        assertEquals(SocialStatus.DENIED, getSocialStatus(added.getSocialEntries(), SocialNetworkType.GITHUB));
        assertEquals(SocialStatus.PENDING, getSocialStatus(added.getSocialEntries(), SocialNetworkType.TWITTER));
        assertEquals(SocialStatus.PENDING, getSocialStatus(added.getSocialEntries(), SocialNetworkType.FACEBOOK));
    }

    @Test
    void test_addSocialEntriesPending_with_equal_entries() {
        Candidate candidate = Candidate.builder()
                .name("allan")
                .socialEntries(Collections.singletonList(
                        SocialEntry.builder().status(SocialStatus.GRANTED).type(SocialNetworkType.LINKEDIN).build()
                ))
                .build();

        List<SocialNetworkType> socialNetworkTypes = Arrays.asList(SocialNetworkType.TWITTER, SocialNetworkType.LINKEDIN);
        Candidate added = candidate.addSocialEntriesPending(socialNetworkTypes);
        assertEquals(2, added.getSocialEntries().size());
        assertEquals(SocialStatus.PENDING, getSocialStatus(added.getSocialEntries(), SocialNetworkType.LINKEDIN));
        assertEquals(SocialStatus.PENDING, getSocialStatus(added.getSocialEntries(), SocialNetworkType.TWITTER));
    }

    @Test
    void test_addSocialEntriesPending_entries_are_null() {
        Candidate candidate = Candidate.builder()
                .name("allan")
                .build();

        List<SocialNetworkType> socialNetworkTypes = Arrays.asList(SocialNetworkType.TWITTER, SocialNetworkType.LINKEDIN);
        Candidate added = candidate.addSocialEntriesPending(socialNetworkTypes);
        assertEquals(2, added.getSocialEntries().size());
        assertEquals(SocialStatus.PENDING, getSocialStatus(added.getSocialEntries(), SocialNetworkType.LINKEDIN));
        assertEquals(SocialStatus.PENDING, getSocialStatus(added.getSocialEntries(), SocialNetworkType.TWITTER));
    }

    @Test
    void test_addSocialEntriesPending_adding_null() {
        Candidate candidate = Candidate.builder()
                .name("allan")
                .socialEntries(Collections.singletonList(
                        SocialEntry.builder().status(SocialStatus.GRANTED).type(SocialNetworkType.LINKEDIN).build()
                ))
                .build();

        Candidate added = candidate.addSocialEntriesPending(null);
        assertEquals(1, added.getSocialEntries().size());
        assertEquals(SocialStatus.GRANTED, getSocialStatus(added.getSocialEntries(), SocialNetworkType.LINKEDIN));
    }

    @Test
    void test_addSocialEntriesPending_adding_empty() {
        Candidate candidate = Candidate.builder()
                .name("allan")
                .socialEntries(Collections.singletonList(
                        SocialEntry.builder().status(SocialStatus.GRANTED).type(SocialNetworkType.LINKEDIN).build()
                ))
                .build();

        Candidate added = candidate.addSocialEntriesPending(Collections.emptyList());
        assertEquals(1, added.getSocialEntries().size());
        assertEquals(SocialStatus.GRANTED, getSocialStatus(added.getSocialEntries(), SocialNetworkType.LINKEDIN));
    }

    @Test
    void test_addLinkedInData() {
        Candidate candidate = Candidate.builder().email("mail@mail.com").build();
        LinkedInProfile linkedInProfile = new LinkedInProfile("Allan", "Weber", "image");
        Candidate updated = candidate.addLinkedInData(linkedInProfile);
        assertEquals("Allan Weber", updated.getName());
        assertEquals("image", updated.getImage());
        assertEquals("mail@mail.com", updated.getEmail());
    }

    @Test
    void test_addLinkedInData_null() {
        Candidate candidate = Candidate.builder().email("mail@mail.com").build();
        assertThrows(NullPointerException.class, () -> candidate.addLinkedInData(null), "LinkedIn Profile had no data");
    }

    @Test
    void test_addGithubData() {
        Candidate candidate = Candidate.builder().name("initial").build();
        GitHubProfile gitHubProfile = GitHubProfile.builder()
                .name("new name")
                .company("company")
                .bio("my bio")
                .apiProfile("my profile")
                .imageBase64("base64image")
                .location("where I live")
                .build();

        Candidate updated = candidate.addGithubData(gitHubProfile);
        assertEquals(gitHubProfile.getName(), updated.getName());
        assertEquals(gitHubProfile.getImageBase64(), updated.getImage());
    }

    @Test
    void test_addGithubData_null() {
        Candidate candidate = Candidate.builder().email("mail@mail.com").build();
        assertThrows(NullPointerException.class, () -> candidate.addGithubData(null), "Github Profile had no data");
    }

    private SocialStatus getSocialStatus(List<SocialEntry> socialEntries, SocialNetworkType networkType) {
        return socialEntries
                .stream()
                .filter(socialEntry -> socialEntry.getType().equals(networkType))
                .findFirst()
                .map(SocialEntry::getStatus)
                .orElse(null);
    }
}