package com.allanweber.candidatescareer.domain.candidate.repository;

import com.mongodb.BasicDBObject;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CandidateRepositoriesRepository {

    private static final String COLLECTION = "candidate-repository";
    private final MongoTemplate mongoTemplate;

    public Integer countRepositories(String candidateId, String owner) {
        MatchOperation match = Aggregation.match(
                new Criteria("candidateId").is(new ObjectId(candidateId)).and("owner").is(owner)
        );
        ProjectionOperation projectRepos = Aggregation.project("repositories").andExclude("_id");
        UnwindOperation unwindOperation = Aggregation.unwind("$repositories");
        GroupOperation groupOperation = Aggregation.group("_id", "result").count().as("count");

        Aggregation aggregation = Aggregation.newAggregation(match, projectRepos, unwindOperation, groupOperation);
        AggregationResults<BasicDBObject> aggregate = mongoTemplate.aggregate(aggregation, COLLECTION, BasicDBObject.class);
        String count = aggregate.getMappedResults().stream().findFirst().map(item -> item.get("count").toString()).orElse("0");
        return Integer.valueOf(count);
    }
}
