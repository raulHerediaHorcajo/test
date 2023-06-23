package com.example.demo.repository;

import com.example.demo.model.GeneratorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneratorTypeRepository extends JpaRepository<GeneratorType, Long>, JpaSpecificationExecutor<GeneratorType> {
}
