package com.allanweber.candidatescareer.domain.candidate;

import com.allanweber.candidatescareer.domain.candidate.dto.CandidateExperience;
import com.allanweber.candidatescareer.domain.candidate.dto.CandidateProfile;
import com.allanweber.candidatescareer.domain.candidate.specification.StartDateIsBeforeEndDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CandidateRegisterUtils {

    public static final String NOT_FOUND_MESSAGE = "Solicitação de registro não encontrada";
    public static final String CANDIDATE_NOT_FOUND_MESSAGE = "Candidato não encontrado.";
    public static final String INVALID_STATUS_MESSAGE = "Solicitação de registro não é inválida e já foi concluída.";
    public static final String INVALID_DATE_MESSAGE = "Data de início e fim são inválidas para %s.";
    public static final String UNAUTHORIZED_STATUS_MESSAGE = "Você não possui acesso a essa solicitação de registro.";
    public static final String REGISTER_PATH = "candidate-register";
    public static final String REGISTER_ACCEPT = "accept";
    public static final String REGISTER_DENIED = "denied";

    public static Optional<String> validateDates(CandidateProfile registerProfile) {
        List<CandidateExperience> notValidDates = registerProfile.getExperiences().stream()
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
