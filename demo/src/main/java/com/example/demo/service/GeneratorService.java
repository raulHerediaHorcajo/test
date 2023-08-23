package com.example.demo.service;

import com.example.demo.model.Generator;
import com.example.demo.repository.criteria.GeneratorCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface GeneratorService {
    Page<Generator> findAll(GeneratorCriteria filters, Pageable pageable);
    Optional<Generator> findById(long id);
    Generator addGenerator(Generator generator);
    Generator updateGenerator(long id, Generator newGenerator);
    void deleteGenerator(long id);
}