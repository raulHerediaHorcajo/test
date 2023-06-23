package com.example.demo.controller;

import com.example.demo.exception.ErrorInfo;
import com.example.demo.exception.GeneratorTypeNotFoundException;
import com.example.demo.model.GeneratorType;
import com.example.demo.repository.criteria.GeneratorTypeCriteria;
import com.example.demo.service.GeneratorTypeService;
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

@Tag(name = "Generator Type", description = "API related to all aspects about the Generator Type")
@RestController
@RequestMapping("/api/generator-types")
public class GeneratorTypeRestController {

    private final GeneratorTypeService generatorTypeService;

    @Autowired
    public GeneratorTypeRestController(GeneratorTypeService generatorTypeService) {
        this.generatorTypeService = generatorTypeService;
    }

    @Operation(summary = "Get Generator Types")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Generator Types found successfully",
                                        content = { @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Page.class, type = "Page<GeneratorType>")) }),
                            @ApiResponse(responseCode = "401", description = "Unauthorized",
                                        content = @Content),
                            @ApiResponse(responseCode = "403", description = "Forbidden without permission",
                                        content = @Content)
    })
    @GetMapping
    public ResponseEntity<Page<GeneratorType>> getGeneratorTypes(@ParameterObject GeneratorTypeCriteria filters,
                                                                 @ParameterObject Pageable pageable) {
        Page<GeneratorType> generatorTypes = generatorTypeService.findAll(filters, pageable);
        return new ResponseEntity<>(generatorTypes, HttpStatus.OK);
    }

    @Operation(summary = "Get Generator Type by id")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Generator Type found successfully",
                                        content = { @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = GeneratorType.class)) }),
                            @ApiResponse(responseCode = "401", description = "Unauthorized",
                                        content = @Content),
                            @ApiResponse(responseCode = "403", description = "Forbidden without permission",
                                        content = @Content),
                            @ApiResponse(responseCode = "404", description = "Generator Type not found",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))})
    })
    @GetMapping("/{id}")
    public ResponseEntity<GeneratorType> getGeneratorType(@PathVariable long id) {
        GeneratorType generatorType = generatorTypeService.findById(id)
            .orElseThrow(() -> new GeneratorTypeNotFoundException(id));
        return new ResponseEntity<>(generatorType, HttpStatus.OK);
    }

    @Operation(summary = "Add Generator Type")
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Generator Type added successfully",
                                        content = { @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = GeneratorType.class)) }),
                            @ApiResponse(responseCode = "400", description = "Invalid Generator Type at creation",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))}),
                            @ApiResponse(responseCode = "401", description = "Unauthorized",
                                        content = @Content),
                            @ApiResponse(responseCode = "403", description = "Forbidden without permission",
                                        content = @Content),
                            @ApiResponse(responseCode = "422", description = "Duplicate Generator Type",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))})
    })
    @PostMapping
    public ResponseEntity<GeneratorType> addGeneratorType(@Valid @RequestBody GeneratorType generatorType) {
        GeneratorType createdGeneratorType = generatorTypeService.addGeneratorType(generatorType);
        return new ResponseEntity<>(createdGeneratorType, HttpStatus.CREATED);
    }

    @Operation(summary = "Update Generator Type")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Generator Type updated successfully",
                                        content = { @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = GeneratorType.class)) }),
                            @ApiResponse(responseCode = "400", description = "Invalid Generator Type at update",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))}),
                            @ApiResponse(responseCode = "401", description = "Unauthorized",
                                        content = @Content),
                            @ApiResponse(responseCode = "403", description = "Forbidden without permission",
                                        content = @Content),
                            @ApiResponse(responseCode = "404", description = "Generator Type not found",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))}),
                            @ApiResponse(responseCode = "422", description = "Duplicate Generator Type",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))})
    })
    @PutMapping("/{id}")
    public ResponseEntity<GeneratorType> updateGeneratorType(@PathVariable long id, @Valid @RequestBody GeneratorType newGeneratorType) {
        GeneratorType updatedGeneratorType = generatorTypeService.updateGeneratorType(id, newGeneratorType);
        return new ResponseEntity<>(updatedGeneratorType, HttpStatus.OK);
    }

    @Operation(summary = "Delete Generator Type")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Generator Type deleted successfully (No Content)"),
                            @ApiResponse(responseCode = "401", description = "Unauthorized",
                                        content = @Content),
                            @ApiResponse(responseCode = "403", description = "Forbidden without permission",
                                        content = @Content),
                            @ApiResponse(responseCode = "404", description = "Generator Type not found",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))})
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGeneratorType(@PathVariable long id) {
        generatorTypeService.deleteGeneratorType(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
