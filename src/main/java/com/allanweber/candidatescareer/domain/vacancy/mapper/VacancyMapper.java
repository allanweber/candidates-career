package com.allanweber.candidatescareer.domain.vacancy.mapper;

import com.allanweber.candidatescareer.domain.vacancy.dto.VacancyDto;
import com.allanweber.candidatescareer.domain.vacancy.repository.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VacancyMapper {

    @Mapping(target = "id", source = "id")
    Vacancy toEntity(VacancyDto dto);

    @Mapping(target = "id", source = "id")
    VacancyDto toDto(Vacancy entity);

    @Mapping(target = "id", source = "id")
    Vacancy mapToUpdate(String id, VacancyDto dto);
}
