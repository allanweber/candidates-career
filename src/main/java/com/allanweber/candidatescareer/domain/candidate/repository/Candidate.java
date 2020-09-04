package com.allanweber.candidatescareer.domain.candidate.repository;

import com.allanweber.candidatescareer.domain.candidate.dto.SocialEntry;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkDto;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialNetworkType;
import com.allanweber.candidatescareer.domain.candidate.dto.SocialStatus;
import com.allanweber.candidatescareer.domain.social.github.dto.GitHubProfile;
import com.allanweber.candidatescareer.domain.social.linkedin.dto.LinkedInProfile;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.*;
import java.util.stream.Collectors;

import static com.allanweber.candidatescareer.domain.candidate.dto.SocialStatus.PENDING;

@Document(collection = "candidate")
@RequiredArgsConstructor
@Getter
@With
@Builder
@EqualsAndHashCode
public class Candidate {
    @Id
    private final String id;

    @NotBlank
    private final String owner;

    private final String name;

    private final String email;

    private final String image;

    private final String location;

    private final String bio;

    private final String currentCompany;

    private final List<SocialNetworkDto> socialNetwork;

    private final List<SocialEntry> socialEntries;

    public Candidate addSocialEntriesPending(List<SocialNetworkType> entries) {
        List<SocialEntry> list = Optional.ofNullable(entries).orElse(Collections.emptyList())
                .stream()
                .map(entry -> SocialEntry.builder().type(entry).status(PENDING).build()).collect(Collectors.toList());
        return addSocialEntries(list);
    }

    public Candidate addSocialEntries(List<SocialEntry> entries) {
        List<SocialEntry> socialEntries = new ArrayList<>(Optional.ofNullable(entries).orElse(Collections.emptyList()));
        Candidate candidate = removeEqualSocialEntries(socialEntries.stream().map(SocialEntry::getType).collect(Collectors.toList()));
        socialEntries.addAll(Optional.ofNullable(candidate.getSocialEntries()).orElse(Collections.emptyList()));
        return candidate.withSocialEntries(socialEntries);
    }

    public Candidate addSocialNetwork(List<SocialNetworkDto> newSocialNetworks) {
        List<SocialNetworkDto> current = new ArrayList<>(Optional.ofNullable(newSocialNetworks).orElse(Collections.emptyList()));
        Candidate candidate = removeEqualSocialNetwork(current.stream().map(SocialNetworkDto::getType).collect(Collectors.toList()));
        current.addAll(Optional.ofNullable(candidate.getSocialNetwork()).orElse(Collections.emptyList()));
        return candidate.withSocialNetwork(current);
    }

    public Candidate markSocialEntry(SocialNetworkType type, SocialStatus status) {
        if (Optional.ofNullable(socialEntries).isPresent()) {
            for (SocialEntry socialEntry : socialEntries) {
                if (socialEntry.getType().equals(type)) {
                    socialEntry.setStatus(status);
                    break;
                }
            }
        }
        return this;
    }

    public Candidate addLinkedInData(LinkedInProfile linkedInProfile) {
        Objects.requireNonNull(linkedInProfile, "LinkedIn Profile had no data");
        String name = Optional.ofNullable(linkedInProfile.getFirstName())
                .map(firstName -> firstName.concat(" ").concat(Optional.of(linkedInProfile.getLastName()).orElse("")))
                .orElse(this.name).trim();
        return this.withName(name).withImage(Optional.ofNullable(linkedInProfile.getProfilePicture()).orElse(image));
    }

    public Candidate addGithubData(GitHubProfile githubProfile) {
        Objects.requireNonNull(githubProfile, "Github Profile had no data");

        return this.withName(githubProfile.getName())
                .withLocation(Optional.ofNullable(githubProfile.getLocation()).orElse(location))
                .withBio(Optional.ofNullable(githubProfile.getBio()).orElse(bio))
                .withCurrentCompany(Optional.ofNullable(githubProfile.getCompany()).orElse(currentCompany))
                .withImage(Optional.ofNullable(githubProfile.getImageBase64()).orElse(image))
                .addSocialNetwork(Collections.singletonList(SocialNetworkDto.builder().type(SocialNetworkType.GITHUB).url(githubProfile.getGithubProfile()).build()));
    }

    private Candidate removeEqualSocialEntries(List<SocialNetworkType> entries) {
        Candidate candidate = this;
        if (Objects.nonNull(socialEntries)) {
            List<SocialEntry> toRemove = socialEntries
                    .stream()
                    .filter(entry -> entries.stream().anyMatch(e -> e.equals(entry.getType())))
                    .collect(Collectors.toList());
            List<SocialEntry> newList = new ArrayList<>(this.socialEntries);
            newList.removeAll(toRemove);
            candidate = this.withSocialEntries(newList);
        }
        return candidate;
    }

    private Candidate removeEqualSocialNetwork(List<SocialNetworkType> entries) {
        Candidate candidate = this;
        if (Objects.nonNull(socialNetwork)) {
            List<SocialNetworkDto> toRemove = socialNetwork
                    .stream()
                    .filter(entry -> entries.stream().anyMatch(e -> e.equals(entry.getType())))
                    .collect(Collectors.toList());
            List<SocialNetworkDto> newList = new ArrayList<>(this.socialNetwork);
            newList.removeAll(toRemove);
            candidate = this.withSocialNetwork(newList);
        }
        return candidate;
    }
}
