package com.allanweber.candidatescareer.domain.vacancy.mapper;

import com.allanweber.candidatescareer.domain.vacancy.dto.Skill;
import com.allanweber.candidatescareer.domain.vacancy.dto.VacancyDto;
import com.allanweber.candidatescareer.domain.vacancy.repository.Vacancy;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class VacancyMapperTest {

    @Test
    void test_toEntity() {
        VacancyDto dto = VacancyDto.builder()
                .name("name")
                .skills(Arrays.asList(getSkill("JAVA"), getSkill("SQL")))
                .build();

        Vacancy vacancy = VacancyMapper.toEntity(dto);
        assertEquals(dto.getName(), vacancy.getName());
        assertEquals(dto.getSkills(), vacancy.getSkills());
        assertNull(vacancy.getId());
    }

    @Test
    void test_toEntity_null_dto() {
        Vacancy vacancy = VacancyMapper.toEntity(null);
        assertNull(vacancy.getName());
        assertNull(vacancy.getSkills());
        assertNull(vacancy.getId());
    }

    @Test
    void test_toResponse() {
        Vacancy entity = Vacancy.builder()
                .id("id")
                .name("name")
                .skills(Arrays.asList(getSkill("JAVA"), getSkill("SQL")))
                .build();

        VacancyDto vacancyDto = VacancyMapper.toResponse(entity);
        assertEquals(entity.getName(), vacancyDto.getName());
        assertEquals(entity.getSkills(), vacancyDto.getSkills());
        assertEquals(entity.getId(), vacancyDto.getId());
    }

    @Test
    void test_toResponse_nul_entity() {
        VacancyDto vacancyDto = VacancyMapper.toResponse(null);
        assertNull(vacancyDto.getName());
        assertNull(vacancyDto.getSkills());
        assertNull(vacancyDto.getId());
    }

    @Test
    void test_mapToUpdate() {
        Vacancy entity = Vacancy.builder()
                .id("id")
                .name("name")
                .skills(Arrays.asList(getSkill("JAVA"), getSkill("SQL")))
                .build();

        VacancyDto dto = VacancyDto.builder()
                .name("my name")
                .skills(Arrays.asList(getSkill("JAVA"), getSkill("SQL")))
                .build();

        Vacancy update = VacancyMapper.mapToUpdate(entity, dto);
        assertEquals("id", update.getId());
        assertEquals("my name", update.getName());
        assertEquals(2, update.getSkills().size());
    }

    private Skill getSkill(String java) {
        return new Skill(java, 1);
    }
}