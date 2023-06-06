package com.example.demo.controller;

import com.example.demo.exception.ErrorInfo;
import com.example.demo.exception.SocietyNotFoundException;
import com.example.demo.model.Society;
import com.example.demo.repository.criteria.SocietyCriteria;
import com.example.demo.service.SocietyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Society", description = "API related to all aspects about the Society")
@RestController
@RequestMapping("/api/societies")
public class SocietyRestController {

    private final SocietyService societyService;

    @Autowired
    public SocietyRestController(SocietyService societyService) {
        this.societyService = societyService;
    }

    @Operation(summary = "Get Societies")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Societies found successfully",
                                        content = { @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Page.class, type = "Page<Society>")) }),
                            @ApiResponse(responseCode = "401", description = "Unauthorized",
                                        content = @Content),
                            @ApiResponse(responseCode = "403", description = "Forbidden without permission",
                                        content = @Content)
    })
    @GetMapping
    public ResponseEntity<Page<Society>> getSocieties(@ParameterObject SocietyCriteria filters,
                                                      @ParameterObject Pageable pageable) {
        Page<Society> societies = societyService.findAll(filters, pageable);
        return new ResponseEntity<>(societies, HttpStatus.OK);
    }

    @Operation(summary = "Get Society by id")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Society found successfully",
                                        content = { @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Society.class)) }),
                            @ApiResponse(responseCode = "401", description = "Unauthorized",
                                        content = @Content),
                            @ApiResponse(responseCode = "403", description = "Forbidden without permission",
                                        content = @Content),
                            @ApiResponse(responseCode = "404", description = "Society not found",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))})
    })
    @GetMapping("/{id}")
    public ResponseEntity<Society> getSociety(@PathVariable long id) {
        Society society = societyService.findById(id)
            .orElseThrow(() -> new SocietyNotFoundException(id));
        return new ResponseEntity<>(society, HttpStatus.OK);
    }

    @Operation(summary = "Add Society")
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Society added successfully",
                                        content = { @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Society.class)) }),
                            @ApiResponse(responseCode = "400", description = "Invalid Society at creation",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))}),
                            @ApiResponse(responseCode = "401", description = "Unauthorized",
                                        content = @Content),
                            @ApiResponse(responseCode = "403", description = "Forbidden without permission",
                                        content = @Content),
                            @ApiResponse(responseCode = "422", description = "Duplicate Society",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))})
    })
    @PostMapping
    public ResponseEntity<Society> addSociety(@Valid @RequestBody Society society) {
        Society createdSociety = societyService.addSociety(society);
        return new ResponseEntity<>(createdSociety, HttpStatus.CREATED);
    }

    @Operation(summary = "Update Society")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Society updated successfully",
                                        content = { @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Society.class)) }),
                            @ApiResponse(responseCode = "400", description = "Invalid Society at update",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))}),
                            @ApiResponse(responseCode = "401", description = "Unauthorized",
                                        content = @Content),
                            @ApiResponse(responseCode = "403", description = "Forbidden without permission",
                                        content = @Content),
                            @ApiResponse(responseCode = "404", description = "Society not found",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))}),
                            @ApiResponse(responseCode = "422", description = "Duplicate Society",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))})
    })
    @PutMapping("/{id}")
    public ResponseEntity<Society> updateSociety(@PathVariable long id, @Valid @RequestBody Society newSociety) {
        Society updatedSociety = societyService.updateSociety(id, newSociety);
        return new ResponseEntity<>(updatedSociety, HttpStatus.OK);
    }

    @Operation(summary = "Delete Society")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Society deleted successfully (No Content)"),
                            @ApiResponse(responseCode = "401", description = "Unauthorized",
                                        content = @Content),
                            @ApiResponse(responseCode = "403", description = "Forbidden without permission",
                                        content = @Content),
                            @ApiResponse(responseCode = "404", description = "Society not found",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))})
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSociety(@PathVariable long id) {
        societyService.deleteSociety(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
