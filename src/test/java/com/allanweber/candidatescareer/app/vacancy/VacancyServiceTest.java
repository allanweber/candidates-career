package com.allanweber.candidatescareer.app.vacancy;

import com.allanweber.candidatescareer.app.vacancy.dto.Skill;
import com.allanweber.candidatescareer.app.vacancy.dto.VacancyDto;
import com.allanweber.candidatescareer.app.vacancy.repository.Vacancy;
import com.allanweber.candidatescareer.app.vacancy.repository.VacancyAuthenticatedRepository;
import com.allanweber.candidatescareer.app.vacancy.service.VacancyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class VacancyServiceTest {

    @Mock
    VacancyAuthenticatedRepository repository;

    @InjectMocks
    VacancyService service;

    @Test
    void getAll() {
        when(repository.findAll()).thenReturn(mockEntities());
        List<VacancyDto> dto = service.getAll();
        assertEquals(3, dto.size());
    }

    @Test
    void getById() {
        Vacancy entity = mockEntities().get(0);
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
        VacancyDto dto = service.getById(entity.getId());
        assertNotNull(dto);
    }

    @Test
    void getById_notFound() {
        when(repository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(HttpClientErrorException.class, () -> service.getById("id"));
    }

    @Test
    void update() {
        Vacancy entity = mockEntities().get(0);
        VacancyDto vacancyDto = VacancyDto.builder().name("NET").skills(Arrays.asList(getSkill(".NET"), getSkill("SQL"))).build();
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(repository.save(eq(entity))).thenReturn(entity);
        VacancyDto dto = service.update(entity.getId(), vacancyDto);
        assertNotNull(dto);
    }

    @Test
    void update_notFound() {
        VacancyDto vacancyDto = VacancyDto.builder().build();
        when(repository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(HttpClientErrorException.class, () -> service.update("", vacancyDto));
    }

    @Test
    void insert() {
        Vacancy entity = mockEntities().get(0);
        VacancyDto vacancyDto = VacancyDto.builder().name("NET").skills(Arrays.asList(getSkill("JAVA"), getSkill("SQL"))).build();
        when(repository.save(eq(entity))).thenReturn(entity);
        VacancyDto dto = service.insert(vacancyDto);
        assertNotNull(dto);
    }

    @Test
    void delete() {
        Vacancy entity = mockEntities().get(0);
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
        doNothing().when(repository).deleteById(entity.getId());
        service.delete(entity.getId());
    }

    @Test
    void delete_notFound() {
        when(repository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(HttpClientErrorException.class, () -> service.delete(""));
    }

    List<Vacancy> mockEntities() {
        return Arrays.asList(
                Vacancy.builder().id(UUID.randomUUID().toString()).name("NET").skills(Arrays.asList(getSkill(".NET"), getSkill("SQL"))).build(),
                Vacancy.builder().id(UUID.randomUUID().toString()).name("NET").skills(Arrays.asList(getSkill("JAVA"), getSkill("SQL"))).build(),
                Vacancy.builder().id(UUID.randomUUID().toString()).name("NET").skills(Arrays.asList(getSkill("PYTHON"), getSkill("SQL"))).build()
        );
    }

    private Skill getSkill(String s) {
        return new Skill(s, 1);
    }
}