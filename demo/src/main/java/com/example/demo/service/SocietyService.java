package com.example.demo.service;

import com.example.demo.model.Society;
import com.example.demo.repository.criteria.SocietyCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SocietyService {
    Page<Society> findAll(SocietyCriteria filters, Pageable pageable);
    Optional<Society> findById(long id);
    Society addSociety(Society society);
    Society updateSociety(long id, Society newSociety);
    void deleteSociety(long id);
}
