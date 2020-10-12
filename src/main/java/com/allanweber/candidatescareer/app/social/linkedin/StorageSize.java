package com.allanweber.candidatescareer.app.social.linkedin;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
class StorageSize {
    private int width;
    private int height;
}
