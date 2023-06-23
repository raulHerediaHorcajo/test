package com.example.demo.service;

import com.example.demo.exception.GeneratorTypeNotFoundException;
import com.example.demo.model.GeneratorType;
import com.example.demo.repository.GeneratorTypeRepository;
import com.example.demo.repository.criteria.GeneratorTypeCriteria;
import com.example.demo.repository.specification.GeneratorTypeSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GeneratorTypeServiceImpl implements GeneratorTypeService {

    private final GeneratorTypeRepository generatorTypeRepository;

    @Autowired
    public GeneratorTypeServiceImpl(GeneratorTypeRepository generatorTypeRepository) {
        this.generatorTypeRepository = generatorTypeRepository;
    }

    @Override
    public Page<GeneratorType> findAll(GeneratorTypeCriteria filters, Pageable pageable) {
        Specification<GeneratorType> specification = new GeneratorTypeSpecification(filters);
        return generatorTypeRepository.findAll(specification, pageable);
    }

    @Override
    public Optional<GeneratorType> findById(long id) {
        return generatorTypeRepository.findById(id);
    }

    @Override
    public GeneratorType addGeneratorType(GeneratorType generatorType) {
        return generatorTypeRepository.save(generatorType);
    }

    @Override
    public GeneratorType updateGeneratorType(long id, GeneratorType newGeneratorType) {
        GeneratorType oldGeneratorType = generatorTypeRepository.findById(id)
            .orElseThrow(() -> new GeneratorTypeNotFoundException(id));
        newGeneratorType.setId(oldGeneratorType.getId());
        return generatorTypeRepository.save(newGeneratorType);
    }


    @Override
    public void deleteGeneratorType(long id) {
        GeneratorType generatorType = generatorTypeRepository.findById(id)
            .orElseThrow(() -> new GeneratorTypeNotFoundException(id));
        generatorTypeRepository.delete(generatorType);
    }
}
