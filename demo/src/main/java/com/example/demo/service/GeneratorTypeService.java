package com.example.demo.service;

import com.example.demo.model.GeneratorType;
import com.example.demo.repository.criteria.GeneratorTypeCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface GeneratorTypeService {
    Page<GeneratorType> findAll(GeneratorTypeCriteria filters, Pageable pageable);
    Optional<GeneratorType> findById(long id);
    GeneratorType addGeneratorType(GeneratorType generatorType);
    GeneratorType updateGeneratorType(long id, GeneratorType newGeneratorType);
    void deleteGeneratorType(long id);
}
