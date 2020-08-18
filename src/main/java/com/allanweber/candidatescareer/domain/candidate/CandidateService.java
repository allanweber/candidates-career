package com.allanweber.candidatescareer.domain.candidate;

import com.allanweber.candidatescareer.domain.candidate.dto.CandidateDto;
import com.allanweber.candidatescareer.domain.candidate.mapper.CandidateMapper;
import com.allanweber.candidatescareer.domain.candidate.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository repository;
    private final CandidateMapper mapper = Mappers.getMapper(CandidateMapper.class);

    public List<CandidateDto> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public CandidateDto getById(String id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND));
    }

    public CandidateDto update(String id, CandidateDto body) {
        return repository.findById(id)
                .map(entity -> mapper.mapToUpdate(id, body))
                .map(repository::save)
                .map(mapper::toDto)
                .orElseThrow(() -> new HttpClientErrorException(NOT_FOUND));
    }

    public CandidateDto insert(CandidateDto body) {
        var entity = repository.insert(mapper.toEntity(body));
        return mapper.toDto(entity);
    }

    public void delete(String id) {
        repository.findById(id).orElseThrow(() -> new HttpClientErrorException(NOT_FOUND));
        repository.deleteById(id);
    }
}
