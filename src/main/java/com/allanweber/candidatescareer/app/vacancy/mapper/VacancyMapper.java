package com.allanweber.candidatescareer.app.vacancy.mapper;

import com.allanweber.candidatescareer.app.vacancy.dto.VacancyDto;
import com.allanweber.candidatescareer.app.vacancy.repository.Vacancy;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VacancyMapper {

    public static Vacancy toEntity(VacancyDto dto) {
        VacancyDto vacancyDto = Optional.ofNullable(dto).orElse(new VacancyDto());
        return Vacancy
                .builder()
                .name(vacancyDto.getName())
                .skills(vacancyDto.getSkills())
                .description(vacancyDto.getDescription())
                .build();
    }

    public static VacancyDto toResponse(Vacancy entity) {
        VacancyDto dto;
        if (Objects.isNull(entity)) {
            dto = VacancyDto.builder().build();
        } else {
            dto = VacancyDto
                    .builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .skills(entity.getSkills())
                    .description(entity.getDescription())
                    .build();
        }
        return dto;
    }

    public static Vacancy mapToUpdate(Vacancy entity, VacancyDto dto) {
        return entity.withName(dto.getName()).withDescription(dto.getDescription()).withSkills(dto.getSkills());
    }
}