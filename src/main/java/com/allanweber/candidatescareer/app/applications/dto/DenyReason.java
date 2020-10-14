package com.allanweber.candidatescareer.app.applications.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class DenyReason {

    @NonNull
    private ApplicationStatus option;

    @NonNull
    private String optionText;

    private String extraReason;
}
