package com.allanweber.candidates_career_recruiter.app.candidate_repositories.repository;

import com.allanweber.candidates_career_recruiter.app.candidate_repositories.dto.GithubRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CandidateRepository extends PagingAndSortingRepository<CandidateRepositories, String> {

    @Aggregation(pipeline = {
            "{ $match: { candidateId: '?0' } }",
            "{ $project: { repositories : 1 , _id : 0 }}",
            "{ $unwind : '$repositories'}",
            "{ $replaceRoot: { newRoot: '$repositories' } }"
    })
    List<GithubRepository> getByCandidateId(String candidateId, Pageable page);
}
