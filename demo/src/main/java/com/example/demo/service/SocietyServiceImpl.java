package com.example.demo.service;

import com.example.demo.model.Society;
import com.example.demo.repository.SocietyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Society addSociety(Society society) {
        return societyRepository.save(society);
    }
}
