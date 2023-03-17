package com.example.demo.controller;

import com.example.demo.exception.SocietyNotFoundException;
import com.example.demo.model.Society;
import com.example.demo.service.SocietyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/societies")
public class SocietyRestController {

    @Autowired
    private SocietyService societyService;

    @GetMapping
    public ResponseEntity<Page<Society>> getSocieties(@RequestParam Map<String, Object> filters,
                                                      Pageable pageable) {
        Page<Society> entities = societyService.findAll(filters, pageable);
        return ResponseEntity.ok(entities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Society> getSociety(@PathVariable long id) {
        Society society = societyService.findById(id)
            .orElseThrow(() -> new SocietyNotFoundException(id));
        return new ResponseEntity<>(society, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Society> addSociety(@Valid @RequestBody Society society) {
        Society createdSociety = societyService.addSociety(society);
        return new ResponseEntity<>(createdSociety, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Society> updateSociety(@PathVariable long id, @Valid @RequestBody Society newSociety) {
        Society updatedSociety = societyService.updateSociety(id, newSociety);
        return new ResponseEntity<>(updatedSociety, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSociety(@PathVariable long id) {
        societyService.deleteSociety(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
