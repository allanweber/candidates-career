package com.allanweber.candidatescareer.api;

import com.allanweber.candidatescareer.api.helpers.VacancyApiTestHelper;
import com.allanweber.candidatescareer.domain.vacancy.dto.VacancyDto;
import com.allanweber.candidatescareer.domain.vacancy.repository.VacancyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("inttest")
class VacancyApiIntegratedTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VacancyRepository repository;

    private VacancyApiTestHelper testHelper;

    @BeforeEach
    public void setUp() {
        this.testHelper = new VacancyApiTestHelper(repository, mockMvc);
        this.testHelper.deleteAll();
    }

    @Test
    void test_happy_path() throws Exception {

        // Is 0
        var all = testHelper.getAll();
        assertTrue(all.isEmpty());

        //Create first
        var createDto = VacancyDto.builder().name("Java").skills(Arrays.asList("Java", "Maven")).build();
        var created1 = testHelper.create(createDto);
        assertEquals(createDto.getName(), created1.getName());
        assertEquals(createDto.getSkills(), created1.getSkills());
        assertFalse(created1.getId().isEmpty());

        // Is 1
        all = testHelper.getAll();
        assertFalse(all.isEmpty());
        assertEquals(created1, all.get(0));

        var get = testHelper.getOne(created1.getId());
        assertEquals(created1, get);

        // Update
        var updateDto = VacancyDto.builder().name("Java Senior").skills(Arrays.asList("Java", "Maven", "Senior")).build();
        testHelper.update(created1.getId(), updateDto);

        // Is 1
        all = testHelper.getAll();
        assertFalse(all.isEmpty());
        assertEquals(1, all.size());
        assertEquals("Java Senior", all.get(0).getName());
        assertEquals(3, all.get(0).getSkills().size());

        var createDto2 = VacancyDto.builder().name(".NET").skills(Arrays.asList("C#", "Entity")).build();
        var created2 = testHelper.create(createDto2);
        assertEquals(createDto2.getName(), created2.getName());
        assertEquals(createDto2.getSkills(), created2.getSkills());
        assertFalse(created2.getId().isEmpty());

        // Is 2
        all = testHelper.getAll();
        assertEquals(2, all.size());

        get = testHelper.getOne(created2.getId());
        assertEquals(created2, get);

        testHelper.delete(created1.getId());

        // Is 1
        all = testHelper.getAll();
        assertEquals(1, all.size());

        testHelper.delete(created2.getId());

        // Is 0
        all = testHelper.getAll();
        assertTrue(all.isEmpty());
    }
}
