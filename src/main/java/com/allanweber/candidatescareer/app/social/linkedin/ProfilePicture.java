package com.allanweber.candidatescareer.app.social.linkedin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
class ProfilePicture {

    @JsonProperty("displayImage~")
    private DisplayImage displayImage;
}
