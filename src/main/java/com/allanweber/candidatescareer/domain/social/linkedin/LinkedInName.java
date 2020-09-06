package com.allanweber.candidatescareer.domain.social.linkedin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
class LinkedInName {
    private Map<String, String> name;

    @JsonCreator
    public LinkedInName(@JsonProperty("localized") Map<String, String> name) {
        this.name = name;
    }
}
