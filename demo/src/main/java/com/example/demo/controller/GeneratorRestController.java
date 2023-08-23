package com.example.demo.controller;

import com.example.demo.exception.ErrorInfo;
import com.example.demo.exception.GeneratorNotFoundException;
import com.example.demo.model.Generator;
import com.example.demo.repository.criteria.GeneratorCriteria;
import com.example.demo.service.GeneratorService;
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

@Tag(name = "Generator", description = "API related to all aspects about the Generator")
@RestController
@RequestMapping("/api/generators")
public class GeneratorRestController {

    private final GeneratorService generatorService;

    @Autowired
    public GeneratorRestController(GeneratorService generatorService) {
        this.generatorService = generatorService;
    }

    @Operation(summary = "Get Generators")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Generators found successfully",
                                        content = { @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Page.class, type = "Page<Generator>")) }),
                            @ApiResponse(responseCode = "401", description = "Unauthorized",
                                        content = @Content),
                            @ApiResponse(responseCode = "403", description = "Forbidden without permission",
                                        content = @Content)
    })
    @GetMapping
    public ResponseEntity<Page<Generator>> getGenerators(@ParameterObject GeneratorCriteria filters,
                                                      @ParameterObject Pageable pageable) {
        Page<Generator> generators = generatorService.findAll(filters, pageable);
        return new ResponseEntity<>(generators, HttpStatus.OK);
    }

    @Operation(summary = "Get Generator by id")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Generator found successfully",
                                        content = { @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Generator.class)) }),
                            @ApiResponse(responseCode = "401", description = "Unauthorized",
                                        content = @Content),
                            @ApiResponse(responseCode = "403", description = "Forbidden without permission",
                                        content = @Content),
                            @ApiResponse(responseCode = "404", description = "Generator not found",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))})
    })
    @GetMapping("/{id}")
    public ResponseEntity<Generator> getGenerator(@PathVariable long id) {
        Generator generator = generatorService.findById(id)
            .orElseThrow(() -> new GeneratorNotFoundException(id));
        return new ResponseEntity<>(generator, HttpStatus.OK);
    }

    @Operation(summary = "Add Generator")
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Generator added successfully",
                                        content = { @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Generator.class)) }),
                            @ApiResponse(responseCode = "400", description = "Invalid Generator at creation",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))}),
                            @ApiResponse(responseCode = "401", description = "Unauthorized",
                                        content = @Content),
                            @ApiResponse(responseCode = "403", description = "Forbidden without permission",
                                        content = @Content),
                            @ApiResponse(responseCode = "422", description = "Duplicate Generator",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))})
    })
    @PostMapping
    public ResponseEntity<Generator> addGenerator(@Valid @RequestBody Generator generator) {
        Generator createdGenerator = generatorService.addGenerator(generator);
        return new ResponseEntity<>(createdGenerator, HttpStatus.CREATED);
    }

    @Operation(summary = "Update Generator")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Generator updated successfully",
                                        content = { @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Generator.class)) }),
                            @ApiResponse(responseCode = "400", description = "Invalid Generator at update",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))}),
                            @ApiResponse(responseCode = "401", description = "Unauthorized",
                                        content = @Content),
                            @ApiResponse(responseCode = "403", description = "Forbidden without permission",
                                        content = @Content),
                            @ApiResponse(responseCode = "404", description = "Generator not found",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))}),
                            @ApiResponse(responseCode = "422", description = "Duplicate Generator",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))})
    })
    @PutMapping("/{id}")
    public ResponseEntity<Generator> updateGenerator(@PathVariable long id, @Valid @RequestBody Generator newGenerator) {
        Generator updatedGenerator = generatorService.updateGenerator(id, newGenerator);
        return new ResponseEntity<>(updatedGenerator, HttpStatus.OK);
    }

    @Operation(summary = "Delete Generator")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Generator deleted successfully (No Content)"),
                            @ApiResponse(responseCode = "401", description = "Unauthorized",
                                        content = @Content),
                            @ApiResponse(responseCode = "403", description = "Forbidden without permission",
                                        content = @Content),
                            @ApiResponse(responseCode = "404", description = "Generator not found",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))})
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenerator(@PathVariable long id) {
        generatorService.deleteGenerator(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}