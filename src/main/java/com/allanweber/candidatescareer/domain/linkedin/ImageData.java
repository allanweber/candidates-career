package com.allanweber.candidatescareer.domain.linkedin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
class ImageData {

    @JsonProperty("com.linkedin.digitalmedia.mediaartifact.StillImage")
    private StillImage stillImage;

}
