package com.allanweber.candidatescareer.domain.vacancy;

import com.allanweber.candidatescareer.domain.vacancy.dto.VacancyDto;
import com.allanweber.candidatescareer.domain.vacancy.mapper.VacancyMapper;
import com.allanweber.candidatescareer.domain.vacancy.repository.Vacancy;
import com.allanweber.candidatescareer.domain.vacancy.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class VacancyService {

    private final VacancyRepository vacancyRepository;
    private final VacancyMapper mapper = Mappers.getMapper(VacancyMapper.class);

    public List<VacancyDto> getAll() {
        return vacancyRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public VacancyDto getById(String id) {
        return vacancyRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND));
    }

    public VacancyDto update(String id, VacancyDto vacancyDto) {
        return vacancyRepository.findById(id)
                .map(entity -> mapper.mapToUpdate(id, vacancyDto))
                .map(vacancyRepository::save)
                .map(mapper::toDto)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND));
    }

    public VacancyDto insert(VacancyDto vacancyDto) {
        Vacancy entity = vacancyRepository.insert(mapper.toEntity(vacancyDto));
        return mapper.toDto(entity);
    }

    public void delete(String id) {
        vacancyRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(NOT_FOUND));
        vacancyRepository.deleteById(id);
    }
}
