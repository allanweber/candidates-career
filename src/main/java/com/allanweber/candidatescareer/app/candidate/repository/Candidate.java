package com.allanweber.candidatescareer.app.candidate.repository;

import com.allanweber.candidatescareer.app.candidate.dto.*;
import com.allanweber.candidatescareer.app.shared.CandidateExperience;
import com.allanweber.candidatescareer.app.social.dto.GitHubCandidate;
import com.allanweber.candidatescareer.app.social.github.dto.GitHubProfile;
import com.allanweber.candidatescareer.app.social.linkedin.dto.LinkedInProfile;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.allanweber.candidatescareer.app.candidate.dto.SocialStatus.PENDING;

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

    private final String phone;

    private final String image;

    private final String location;

    private final String bio;

    private final String currentCompany;

    private final LocalDateTime lastUpdate;

    private final List<SocialNetworkDto> socialNetwork;

    private final List<SocialEntry> socialEntries;

    private final List<CandidateExperience> experiences;

    private final GitHubCandidate gitHubCandidate;

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
        return markSocialEntry(type, status, null);
    }

    public Candidate markSocialEntry(SocialNetworkType type, SocialStatus status, String error) {
        if (Optional.ofNullable(socialEntries).isPresent()) {
            for (SocialEntry socialEntry : socialEntries) {
                if (socialEntry.getType().equals(type)) {
                    socialEntry.setStatus(status);
                    socialEntry.setError(error);
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

        return this.withName(Optional.ofNullable(githubProfile.getName()).orElse(name))
                .withLocation(Optional.ofNullable(githubProfile.getLocation()).orElse(location))
                .withBio(Optional.ofNullable(githubProfile.getBio()).orElse(bio))
                .withCurrentCompany(Optional.ofNullable(githubProfile.getCompany()).orElse(currentCompany))
                .withImage(Optional.ofNullable(githubProfile.getImageBase64()).orElse(image))
                .addSocialNetwork(Collections.singletonList(SocialNetworkDto.builder().type(SocialNetworkType.GITHUB).url(githubProfile.getGithubProfile()).build()))
                .withGitHubCandidate(githubProfile.getGitHubCandidate());
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
