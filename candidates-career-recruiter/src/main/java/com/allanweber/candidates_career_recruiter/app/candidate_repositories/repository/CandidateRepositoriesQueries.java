package com.allanweber.candidates_career_recruiter.app.candidate_repositories.repository;

import com.allanweber.candidates_career_recruiter.app.candidate_repositories.dto.RepositoryCounter;
import com.mongodb.BasicDBObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CandidateRepositoriesQueries {

    private static final String COLLECTION = "candidate-repository";
    private final MongoTemplate mongoTemplate;

    /**
     * db.getCollection('candidate-repository').aggregate([
     *     { $match: { candidateId: "$ID" } },
     *     { $project: { repositories : 1 , _id : 0 }},
     *     { $unwind : "$repositories" },
     *     { $group: { _id: "result", count: { $sum: 1 }}}
     * ])
     */
    public Integer countRepositories(String candidateId, String owner) {
        MatchOperation match = getMatchOperation(candidateId, owner);
        ProjectionOperation projectRepos = Aggregation.project("repositories").andExclude("_id");
        UnwindOperation unwindOperation = Aggregation.unwind("$repositories");
        GroupOperation groupOperation = Aggregation.group("_id", "result").count().as("count");

        Aggregation aggregation = Aggregation.newAggregation(match, projectRepos, unwindOperation, groupOperation);
        AggregationResults<BasicDBObject> aggregate = mongoTemplate.aggregate(aggregation, COLLECTION, BasicDBObject.class);
        String count = aggregate.getMappedResults().stream().findFirst().map(item -> item.get("count").toString()).orElse("0");
        return Integer.valueOf(count);
    }

    /**
     *db.getCollection('candidate-repository').aggregate([
     *     { $match: { candidateId: "$ID" } },
     *     { $project:
     *         {
     *             "starts": { "$sum": "$repositories.stars"},
     *             "watchers": { "$sum": "$repositories.watchers"},
     *             _id : 0
     *         }
     *      }
     * ])
     */
    public RepositoryCounter countFields(String candidateId, String owner) {
        MatchOperation match = getMatchOperation(candidateId, owner);

        AggregationExpression starsExpression = AccumulatorOperators.Sum.sumOf("$repositories.stars");
        AggregationExpression watchersExpression = AccumulatorOperators.Sum.sumOf("$repositories.watchers");

        ProjectionOperation projectionOperation = Aggregation.project ("someId")
                .and(starsExpression).as("starts")
                .and(watchersExpression).as("watchers")
                .andExclude("_id");

        Aggregation aggregation = Aggregation.newAggregation(match, projectionOperation);
        AggregationResults<BasicDBObject> aggregate = mongoTemplate.aggregate(aggregation, COLLECTION, BasicDBObject.class);
        String starts = aggregate.getMappedResults().stream().findFirst().map(item -> item.get("starts").toString()).orElse("0");
        String watchers= aggregate.getMappedResults().stream().findFirst().map(item -> item.get("watchers").toString()).orElse("0");
        return RepositoryCounter.builder().starts(Long.parseLong(starts)).watchers(Long.parseLong(watchers)).build();
    }

    private MatchOperation getMatchOperation(String candidateId, String owner) {
        return Aggregation.match(
                new Criteria("candidateId").is(candidateId).and("owner").is(owner)
        );
    }
}
