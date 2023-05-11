package com.example.demo.unit.controller;

import com.example.demo.controller.SocietyRestController;
import com.example.demo.exception.SocietyNotFoundException;
import com.example.demo.model.Society;
import com.example.demo.repository.criteria.SocietyCriteria;
import com.example.demo.service.SocietyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SocietyRestControllerUnitTest {

    private SocietyRestController societyRestController;

    @Mock
    private SocietyService societyService;

    @BeforeEach
    public void setUp() {
        societyRestController = new SocietyRestController(societyService);
    }

    @Test
    void testGetSocieties() {
        Pageable pageable = PageRequest.of(0, 20);
        List<Society> societies = List.of(mock(Society.class), mock(Society.class), mock(Society.class));
        Page<Society> page = new PageImpl<>(societies, pageable, 3);
        SocietyCriteria filters = mock(SocietyCriteria.class);
        when(societyService.findAll(filters, pageable)).thenReturn(page);

        ResponseEntity<Page<Society>> result = societyRestController.getSocieties(filters, pageable);

        verify(societyService).findAll(filters, pageable);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getNumberOfElements()).isEqualTo(3);
        assertThat(result.getBody().getContent()).containsAll(societies);
        assertThat(result.getBody().getPageable()).isEqualTo(pageable);
    }

    @Test
    void whenGetNotExistSocietyById_thenShouldGiveSocietyNotFoundException() {
        when(societyService.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> societyRestController.getSociety(1))
            .isInstanceOf(SocietyNotFoundException.class)
            .hasMessageContaining("Society 1 not found");

        verify(societyService).findById(1);
    }

    @Test
    void testGetSocietyById() {
        Society society = mock(Society.class);
        when(societyService.findById(1)).thenReturn(Optional.of(society));

        ResponseEntity<Society> result = societyRestController.getSociety(1);

        verify(societyService).findById(1);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(society);
    }


    @Test
    void testAddSociety() {
        Society society = mock(Society.class);
        when(societyService.addSociety(society)).thenReturn(society);

        ResponseEntity<Society> result = societyRestController.addSociety(society);

        verify(societyService).addSociety(society);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(society);
    }

    @Test
    void testUpdateSociety() {
        Society society = mock(Society.class);
        when(societyService.updateSociety(1, society)).thenReturn(society);

        ResponseEntity<Society> result = societyRestController.updateSociety(1, society);

        verify(societyService).updateSociety(1, society);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(society);
    }

    @Test
    void testDeleteSociety() {
        ResponseEntity<Void> result = societyRestController.deleteSociety(1);

        verify(societyService).deleteSociety(1);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
