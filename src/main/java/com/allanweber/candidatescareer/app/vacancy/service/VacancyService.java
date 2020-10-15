package com.allanweber.candidatescareer.app.vacancy.service;

import com.allanweber.candidatescareer.app.vacancy.dto.VacancyDto;
import com.allanweber.candidatescareer.app.vacancy.mapper.VacancyMapper;
import com.allanweber.candidatescareer.app.vacancy.repository.VacancyAuthenticatedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class VacancyService {

    private static final String VACATION_NOT_FOUND = "Vaga não encontrada.";
    private static final String LOCATION_NOT_FOUND = "Se a vaga não é remota a localização deve ser informada.";

    private final VacancyAuthenticatedRepository repository;

    public List<VacancyDto> getAll() {
        return repository.findAll()
                .stream()
                .map(VacancyMapper::toResponse)
                .collect(Collectors.toList());
    }

    public VacancyDto getById(String id) {
        return repository.findById(id)
                .map(VacancyMapper::toResponse)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, VACATION_NOT_FOUND));
    }

    public VacancyDto update(String id, VacancyDto vacancyDto) {
        validateDto(vacancyDto);
        return repository.findById(id)
                .map(entity -> VacancyMapper.mapToUpdate(entity, vacancyDto))
                .map(repository::save)
                .map(VacancyMapper::toResponse)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, VACATION_NOT_FOUND));
    }

    public VacancyDto insert(VacancyDto vacancyDto) {
        validateDto(vacancyDto);
        var entity = repository.save(VacancyMapper.toEntity(vacancyDto));
        return VacancyMapper.toResponse(entity);
    }

    private void validateDto(VacancyDto vacancyDto) {
        if(!vacancyDto.isRemote() && vacancyDto.getLocation() == null) {
           throw  new HttpClientErrorException(BAD_REQUEST, LOCATION_NOT_FOUND);
        }
    }

    public void delete(String id) {
        repository.findById(id).orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, VACATION_NOT_FOUND));
        repository.deleteById(id);
    }
}
