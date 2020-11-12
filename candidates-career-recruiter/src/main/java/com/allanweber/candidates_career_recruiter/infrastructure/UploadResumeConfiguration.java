package com.allanweber.candidates_career_recruiter.infrastructure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConfigurationProperties(prefix = "app.upload.resume")
@Configuration
@Data
public class UploadResumeConfiguration {
    private long maxSize;

    private List<String> extensionsAllowed;
}
