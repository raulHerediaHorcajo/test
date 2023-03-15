package com.example.demo.service;

import com.example.demo.exception.SocietyNotFoundException;
import com.example.demo.model.Society;
import com.example.demo.repository.SocietyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SocietyServiceImpl implements SocietyService {

    @Autowired
    private SocietyRepository societyRepository;

    public SocietyServiceImpl() {
        //Default empty constructor
    }

    public SocietyServiceImpl(SocietyRepository societyRepository) {
        this.societyRepository = societyRepository;
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
    public void deleteSociety(long id) {
        Society society = societyRepository.findById(id)
            .orElseThrow(() -> new SocietyNotFoundException(id));
        societyRepository.delete(society);
    }
}
