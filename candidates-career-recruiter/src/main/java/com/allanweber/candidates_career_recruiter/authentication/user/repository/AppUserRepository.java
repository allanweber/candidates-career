package com.allanweber.candidates_career_recruiter.authentication.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AppUserRepository extends MongoRepository<AppUser, String> {
    Boolean existsByUserName(String userName);

    Boolean existsByEmail(String email);

    Optional<AppUser> findByEmail(String email);
}
