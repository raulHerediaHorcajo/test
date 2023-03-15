package com.example.demo.service;

import com.example.demo.model.Society;

import java.util.Optional;

public interface SocietyService {
    Optional<Society> findById(long id);
    Society addSociety(Society society);
    void deleteSociety(long id);
}
