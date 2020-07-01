package com.example.gecko.service;

import com.example.gecko.converter.RecordToEntityConverter;
import com.example.gecko.model.Record;
import com.example.gecko.model.SearchRequest;
import com.example.gecko.parser.MultipartFileParser;
import com.example.gecko.repository.RecordEntitySpecificationBuilder;
import com.example.gecko.repository.RecordRepository;
import com.example.gecko.repository.entity.RecordEntity;
import com.example.gecko.exception.RecordNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository repository;
    private final MultipartFileParser parser;
    private final RecordToEntityConverter recordToEntityConverter;
    private final RecordEntitySpecificationBuilder specificationBuilder;

    public List<RecordEntity> validateAndSaveRecords(MultipartFile file) {
        List<Record> records = parser.parse(file);
        List<RecordEntity> entities = records.stream()
                                             .map(recordToEntityConverter::convert)
                                             .collect(Collectors.toList());
        repository.saveAll(entities);
        return entities;
    }

    public RecordEntity findByKey(String key) {
        if (isBlank(key)) {
            return null;
        }
        Optional<RecordEntity> record = repository.findById(key);
        if (!record.isPresent()) {
            throw new RecordNotFoundException();
        }
        return record.get();
    }

    public Page<RecordEntity> findAll(SearchRequest request, Pageable pageable) {
        Specification<RecordEntity> specification = specificationBuilder.build(request);
        return repository.findAll(specification, pageable);
    }

    public void delete(String key) {
        findByKey(key);
        repository.deleteById(key);
    }
}
