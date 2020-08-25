package com.allanweber.candidatescareer.domain.vacancy.mapper;

import com.allanweber.candidatescareer.domain.vacancy.dto.VacancyDto;
import com.allanweber.candidatescareer.domain.vacancy.repository.Vacancy;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VacancyMapper {

    public static Vacancy toEntity(VacancyDto dto) {
        dto = Optional.ofNullable(dto).orElse(new VacancyDto());
        return Vacancy
                .builder()
                .name(dto.getName())
                .skills(dto.getSkills())
                .build();
    }

    public static VacancyDto toResponse(Vacancy entity) {
        if(Objects.isNull(entity)){
            return VacancyDto.builder().build();
        }
        return VacancyDto
                .builder()
                .id(entity.getId())
                .name(entity.getName())
                .skills(entity.getSkills())
                .build();
    }

    public static Vacancy mapToUpdate(Vacancy entity, VacancyDto dto) {
        return entity.withName(dto.getName()).withSkills(dto.getSkills());
    }
}
