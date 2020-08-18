package com.allanweber.candidatescareer.domain.vacancy;

import com.allanweber.candidatescareer.domain.vacancy.dto.VacancyDto;
import com.allanweber.candidatescareer.domain.vacancy.mapper.VacancyMapper;
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

    private final VacancyRepository repository;
    private final VacancyMapper mapper = Mappers.getMapper(VacancyMapper.class);

    public List<VacancyDto> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public VacancyDto getById(String id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND));
    }

    public VacancyDto update(String id, VacancyDto body) {
        return repository.findById(id)
                .map(entity -> mapper.mapToUpdate(id, body))
                .map(repository::save)
                .map(mapper::toDto)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND));
    }

    public VacancyDto insert(VacancyDto body) {
        var entity = repository.insert(mapper.toEntity(body));
        return mapper.toDto(entity);
    }

    public void delete(String id) {
        repository.findById(id).orElseThrow(() -> new HttpClientErrorException(NOT_FOUND));
        repository.deleteById(id);
    }
}
