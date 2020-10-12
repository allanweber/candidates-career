package com.allanweber.candidatescareer.app.applications.service;

import com.allanweber.candidatescareer.app.candidate.dto.CandidateExperience;
import com.allanweber.candidatescareer.app.candidate.dto.CandidateProfile;
import com.allanweber.candidatescareer.app.candidate.specification.StartDateIsBeforeEndDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CandidateApplicationUtils {

    public static final String NOT_FOUND_MESSAGE = "Solicitação de registro não encontrada";
    public static final String CANDIDATE_NOT_FOUND_MESSAGE = "Candidato não encontrado.";
    public static final String INVALID_STATUS_MESSAGE = "Solicitação de registro não é inválida e já foi concluída.";
    public static final String INVALID_DATE_MESSAGE = "Data de início e fim são inválidas para %s.";
    public static final String UNAUTHORIZED_STATUS_MESSAGE = "Você não possui acesso a essa solicitação de registro.";
    public static final String APPLICATION_PATH = "candidate-application";
    public static final String APPLICATION_ACCEPT = "accept";
    public static final String APPLICATION_DENIED = "denied";

    public static Optional<String> validateDates(CandidateProfile candidateProfile) {
        List<CandidateExperience> notValidDates = candidateProfile.getExperiences().stream()
                .filter(exp -> !StartDateIsBeforeEndDate.satisfiedBy().test(exp))
                .collect(Collectors.toList());

        String message = null;
        if(!notValidDates.isEmpty()) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            message = notValidDates.stream()
                    .map(exp ->
                            "Empresa: " + exp.getCompanyName() +
                                    " Cargo: " + exp.getPosition() +
                                    " Data de início: " + exp.getStart().format(dateTimeFormatter) +
                                    " Data de saída: " + exp.getEnd().format(dateTimeFormatter)
                    ).collect(Collectors.joining(" | "));

        }

        return Optional.ofNullable(message);
    }
}
