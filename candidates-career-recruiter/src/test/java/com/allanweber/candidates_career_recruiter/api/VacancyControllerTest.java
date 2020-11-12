package com.allanweber.candidates_career_recruiter.api;

import com.allanweber.candidates_career_recruiter.app.shared.Skill;
import com.allanweber.candidates_career_recruiter.app.vacancy.api.VacancyController;
import com.allanweber.candidates_career_recruiter.app.vacancy.dto.VacancyDto;
import com.allanweber.candidates_career_recruiter.app.vacancy.service.VacancyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class VacancyControllerTest {

    @Mock
    VacancyService service;

    @InjectMocks
    VacancyController controller;

    @Test
    void getAll() {
        when(service.getAll()).thenReturn(mockDtos());
        ResponseEntity<List<VacancyDto>> responseEntity = controller.getAll();
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(3, Objects.requireNonNull(responseEntity.getBody()).size());
    }

    @Test
    void get() {
        VacancyDto dto = mockDtos().get(0);
        when(service.getById(dto.getId())).thenReturn(dto);
        ResponseEntity<VacancyDto> responseEntity = controller.get(dto.getId());
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(dto, Objects.requireNonNull(responseEntity.getBody()));
    }

    @Test
    void create() {
        VacancyDto dto = mockDtos().get(0);
        when(service.insert(dto)).thenReturn(dto);
        ResponseEntity<VacancyDto> responseEntity = controller.create(dto);
        assertEquals(201, responseEntity.getStatusCodeValue());
        assertTrue(responseEntity.getHeaders().containsKey("Location"));
        assertEquals("/vacancies/" + dto.getId(), Objects.requireNonNull(responseEntity.getHeaders().get("Location")).get(0));
        assertEquals(dto, Objects.requireNonNull(responseEntity.getBody()));
    }

    @Test
    void update() {
        VacancyDto dto = mockDtos().get(0);
        when(service.update(dto.getId(), dto)).thenReturn(dto);
        ResponseEntity<VacancyDto> responseEntity = controller.update(dto.getId(), dto);
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(dto, Objects.requireNonNull(responseEntity.getBody()));
    }

    @Test
    void delete() {
        String id = UUID.randomUUID().toString();
        doNothing().when(service).delete(id);
        ResponseEntity<?> responseEntity = controller.delete(id);
        assertEquals(410, responseEntity.getStatusCodeValue());
    }

    List<VacancyDto> mockDtos() {
        return Arrays.asList(
                VacancyDto.builder().id(UUID.randomUUID().toString()).name("NET").skills(Arrays.asList(getSkill(".NET"), getSkill("SQL"))).build(),
                VacancyDto.builder().id(UUID.randomUUID().toString()).name("NET").skills(Arrays.asList(getSkill("JAVA"), getSkill("SQL"))).build(),
                VacancyDto.builder().id(UUID.randomUUID().toString()).name("NET").skills(Arrays.asList(getSkill("PYTHON"), getSkill("SQL"))).build()
        );
    }

    private Skill getSkill(String s) {
        return new Skill(s, 1);
    }
}