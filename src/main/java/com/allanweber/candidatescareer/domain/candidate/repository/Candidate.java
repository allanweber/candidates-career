package com.allanweber.candidatescareer.domain.candidate.repository;

import com.allanweber.candidatescareer.domain.candidate.dto.SocialEntry;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkDto;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType;
import com.allanweber.candidatescareer.domain.linkedin.dto.LinkedInProfile;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.allanweber.candidatescareer.domain.candidate.dto.SocialStatus.GRANTED;
import static com.allanweber.candidatescareer.domain.candidate.dto.SocialStatus.PENDING;

@Document(collection = "candidate")
@RequiredArgsConstructor
@Getter
@With
@Builder
public class Candidate {
    @Id
    private final String id;

    private final String name;

    private final String email;

    private final String image;

    private final List<SocialNetworkDto> socialNetwork;

    private final List<SocialEntry> socialEntries;

    public Candidate addSocialEntriesPending(List<SocialNetworkType> entries) {
        List<SocialEntry> list = entries
                .stream()
                .map(entry -> SocialEntry.builder().type(entry).status(PENDING).build()).collect(Collectors.toList());
        return addSocialEntries(list);
    }

    public Candidate addSocialEntries(List<SocialEntry> entries) {
        List<SocialEntry> socialEntries = Optional.ofNullable(this.socialEntries).orElse(new ArrayList<>());
        socialEntries.addAll(entries);
        return this.withSocialEntries(socialEntries);
    }

    public Candidate removeEqualEntries(List<SocialNetworkType> entries) {
        if (Optional.ofNullable(socialEntries).isPresent()) {
            List<SocialEntry> toRemove = socialEntries
                    .stream()
                    .filter(entry -> entries.stream().anyMatch(e -> e.equals(entry.getType())))
                    .collect(Collectors.toList());
            socialEntries.removeAll(toRemove);
        }
        return this;
    }

    public Candidate markSocialEntryDone(SocialNetworkType type) {
        if (Optional.ofNullable(socialEntries).isPresent()) {
            for (SocialEntry socialEntry : socialEntries) {
                if (socialEntry.getType().equals(type)) {
                    socialEntry.setStatus(GRANTED);
                    break;
                }
            }
        }
        return this;
    }

    public Candidate addLinkedInData(LinkedInProfile linkedInProfile) {
        String name = Optional.ofNullable(linkedInProfile.getFirstName())
                .map(firstName -> firstName.concat(" ").concat(Optional.of(linkedInProfile.getLastName()).orElse("")))
                .orElse(this.name).trim();
        return this.withName(name).withImage(Optional.ofNullable(linkedInProfile.getProfilePicture()).orElse(image));
    }
}
