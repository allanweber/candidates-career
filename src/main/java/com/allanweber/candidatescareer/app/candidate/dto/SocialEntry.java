package com.allanweber.candidatescareer.app.candidate.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
public class SocialEntry {

    private SocialNetworkType type;

    @Setter
    private SocialStatus status;

    @Setter
    private String error;
}
