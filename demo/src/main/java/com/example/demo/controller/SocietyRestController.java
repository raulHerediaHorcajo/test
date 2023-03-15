package com.example.demo.controller;

import com.example.demo.exception.SocietyNotFoundException;
import com.example.demo.model.Society;
import com.example.demo.service.SocietyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/societies")
public class SocietyRestController {

    @Autowired
    private SocietyService societyService;

    @GetMapping("/{id}")
    public ResponseEntity<Society> getSociety(@PathVariable long id) {
        Society society = societyService.findById(id)
            .orElseThrow(() -> new SocietyNotFoundException(id));

        return new ResponseEntity<>(society, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Society> addSociety(@Valid @RequestBody Society society) {
        Society societyCreated = societyService.addSociety(society);
        return new ResponseEntity<>(societyCreated, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSociety(@PathVariable long id) {
        societyService.deleteSociety(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
