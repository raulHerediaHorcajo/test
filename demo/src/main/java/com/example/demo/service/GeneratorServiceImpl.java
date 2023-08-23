package com.example.demo.service;

import com.example.demo.exception.GeneratorNotFoundException;
import com.example.demo.model.Generator;
import com.example.demo.repository.GeneratorRepository;
import com.example.demo.repository.criteria.GeneratorCriteria;
import com.example.demo.repository.specification.GeneratorSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GeneratorServiceImpl implements GeneratorService {

    private final GeneratorRepository generatorRepository;

    @Autowired
    public GeneratorServiceImpl(GeneratorRepository generatorRepository) {
        this.generatorRepository = generatorRepository;
    }

    @Override
    public Page<Generator> findAll(GeneratorCriteria filters, Pageable pageable) {
        Specification<Generator> specification = new GeneratorSpecification(filters);
        return generatorRepository.findAll(specification, pageable);
    }

    @Override
    public Optional<Generator> findById(long id) {
        return generatorRepository.findById(id);
    }

    @Override
    public Generator addGenerator(Generator generator) {
        return generatorRepository.save(generator);
    }

    @Override
    public Generator updateGenerator(long id, Generator newGenerator) {
        Generator oldGenerator = generatorRepository.findById(id)
            .orElseThrow(() -> new GeneratorNotFoundException(id));
        newGenerator.setId(oldGenerator.getId());
        return generatorRepository.save(newGenerator);
    }

    @Override
    public void deleteGenerator(long id) {
        Generator generator = generatorRepository.findById(id)
            .orElseThrow(() -> new GeneratorNotFoundException(id));
        generatorRepository.delete(generator);
    }
}