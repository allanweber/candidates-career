package com.allanweber.candidatescareer.domain.linkedin;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
class DisplayImage {
    private List<Element> elements;
}
