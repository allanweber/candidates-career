package com.allanweber.candidatescareer.authentication.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuppressWarnings("PMD.AvoidFieldNameMatchingTypeName")
public class Authority {
    private String authority;
}
