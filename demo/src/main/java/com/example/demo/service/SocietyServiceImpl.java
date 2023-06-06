package com.example.demo.service;

import com.example.demo.exception.SocietyNotFoundException;
import com.example.demo.model.Society;
import com.example.demo.repository.SocietyRepository;
import com.example.demo.repository.criteria.SocietyCriteria;
import com.example.demo.repository.specification.SocietySpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SocietyServiceImpl implements SocietyService {

    private final SocietyRepository societyRepository;

    @Autowired
    public SocietyServiceImpl(SocietyRepository societyRepository) {
        this.societyRepository = societyRepository;
    }

    @Override
    public Page<Society> findAll(SocietyCriteria filters, Pageable pageable) {
        Specification<Society> specification = new SocietySpecification(filters);
        return societyRepository.findAll(specification, pageable);
    }

    @Override
    public Optional<Society> findById(long id) {
        return societyRepository.findById(id);
    }

    @Override
    public Society addSociety(Society society) {
        return societyRepository.save(society);
    }

    @Override
    public Society updateSociety(long id, Society newSociety) {
        Society oldSociety = societyRepository.findById(id)
            .orElseThrow(() -> new SocietyNotFoundException(id));
        newSociety.setId(oldSociety.getId());
        return societyRepository.save(newSociety);
    }


    @Override
    public void deleteSociety(long id) {
        Society society = societyRepository.findById(id)
            .orElseThrow(() -> new SocietyNotFoundException(id));
        societyRepository.delete(society);
    }
}
