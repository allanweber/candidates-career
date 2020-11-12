package com.allanweber.candidates_career_recruiter.app.candidate.specification;

import com.allanweber.candidates_career_recruiter.app.shared.CandidateExperience;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.function.Predicate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StartDateIsBeforeEndDate {

    public static Predicate<CandidateExperience> satisfiedBy() {
        return experience -> {
            boolean returnValue;
            if (Objects.isNull(experience)) {
                returnValue = false;
            } else if (Objects.isNull(experience.getEnd())) {
                returnValue = true;
            } else {
                returnValue = experience.getStart().isBefore(experience.getEnd());
            }
            return returnValue;
        };
    }
}
