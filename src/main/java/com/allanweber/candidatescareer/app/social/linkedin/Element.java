package com.allanweber.candidatescareer.app.social.linkedin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
class Element {
    private String authorizationMethod;

    @JsonProperty("data")
    private ImageData data;

    private List<IdentifierImage> identifiers;
}