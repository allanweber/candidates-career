package com.allanweber.candidates_career_recruiter.infrastructure.configuration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
@NoArgsConstructor
@Profile("!test")
@SuppressWarnings("PMD.BeanMembersShouldSerialize")
public class MongoConfiguration extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.database:anyDb}")
    private String dataBaseName;

    @Value("${spring.data.mongodb.uri:}")
    private String mongoUriConnection;

    @Override
    protected String getDatabaseName() {
        return dataBaseName;
    }

    @Override
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUriConnection);
    }
}

