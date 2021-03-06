package com.allanweber.candidatescareer.app.candidate.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
public class SocialNetworkDto {
    @NonNull
    private SocialNetworkType type;

    @NotBlank
    @Pattern(regexp = "https?://.+", message = "Git hub url is invalid")
    private String url;
}
