package com.allanweber.candidatescareer.app.applications.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ApplicationStatus {
    PENDING("Pendente", false),
    VIEW("Viu vaga", false),
    ACCEPT("Aceitou vaga", false),
    DONE("Completo", false),
    ERROR("Erro", false),
    DENIED("Negado", false),
    
    NOT_INTERESTING("Não tenho interesse", true),
    NOT_MY_PROFILE("Não é meu perfil", true),
    NOT_MY_SALARY("Não é minha pretensão salarial", true),
    NO_EXPERIENCE("Não tenho experiência suficiente", true),
    OTHER("Outro", true);

    private final String text;
    
    private final boolean choosable;
}
