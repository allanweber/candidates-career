package com.allanweber.candidatescareer.app.vacancy.mapper;

import com.allanweber.candidatescareer.app.vacancy.dto.Salary;
import com.allanweber.candidatescareer.app.vacancy.dto.VacancyDto;
import com.allanweber.candidatescareer.app.vacancy.repository.Vacancy;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VacancyMapper {

    public static Vacancy toEntity(VacancyDto dto) {
        VacancyDto vacancyDto = Optional.ofNullable(dto).orElse(VacancyDto.builder().build());
        return Vacancy
                .builder()
                .name(vacancyDto.getName())
                .skills(vacancyDto.getSkills())
                .description(vacancyDto.getDescription())
                .location(vacancyDto.getLocation())
                .remote(vacancyDto.isRemote())
                .salary(vacancyDto.getSalary())
                .build();
    }

    public static VacancyDto toResponseView(Vacancy entity) {
        VacancyDto.VacancyDtoBuilder builder = getBuilder(entity);
        VacancyDto dto = builder.build();
        if (Optional.ofNullable(dto.getSalary()).map(Salary::getVisible).orElse(false)) {
            if (dto.getSalary().getFrom() == 0 && dto.getSalary().getTo() == 0) {
                builder.salary(null);
            }
        } else {
            builder.salary(null);
        }
        return builder.build();
    }

    public static VacancyDto toResponse(Vacancy entity) {
        return getBuilder(entity).build();
    }

    private static VacancyDto.VacancyDtoBuilder getBuilder(Vacancy entity) {
        VacancyDto.VacancyDtoBuilder builder;
        if (entity == null) {
            builder = VacancyDto.builder();
        } else {
            builder = VacancyDto
                    .builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .skills(entity.getSkills())
                    .description(entity.getDescription())
                    .location(entity.getLocation())
                    .remote(entity.isRemote())
                    .salary(entity.getSalary());
        }
        return builder;
    }

    public static Vacancy mapToUpdate(Vacancy entity, VacancyDto dto) {
        return entity.withName(dto.getName())
                .withDescription(dto.getDescription())
                .withSkills(dto.getSkills())
                .withLocation(dto.getLocation())
                .withRemote(dto.isRemote())
                .withSalary(dto.getSalary());
    }
}
