package com.example.demo.controller;

import com.example.demo.exception.ErrorInfo;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.criteria.UserCriteria;
import com.example.demo.security.config.SecurityExpressions.UserRole;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "API related to all aspects about the User")
@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get Users")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Users found successfully",
                                        content = { @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = Page.class, type = "Page<User>")) }),
                            @ApiResponse(responseCode = "401", description = "Unauthorized",
                                        content = @Content),
                            @ApiResponse(responseCode = "403", description = "Forbidden without permission",
                                        content = @Content)
    })
    @GetMapping
    public ResponseEntity<Page<User>> getUsers(@ParameterObject UserCriteria filters,
                                               @ParameterObject Pageable pageable) {
        Page<User> users = userService.findAll(filters, pageable);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(summary = "Get User by id")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "User found successfully",
                                        content = { @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = User.class)) }),
                            @ApiResponse(responseCode = "401", description = "Unauthorized",
                                        content = @Content),
                            @ApiResponse(responseCode = "403", description = "Forbidden without permission",
                                        content = @Content),
                            @ApiResponse(responseCode = "404", description = "User not found",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))})
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(HttpServletRequest request, @PathVariable long id) {
        User currentUser = userService.getCurrentUser(request)
            .orElseThrow(() -> new AccessDeniedException("You must be logged in to access the account"));
        if (!request.isUserInRole(UserRole.ADMIN.name())
            && currentUser.getId() != id) {
            throw new AccessDeniedException("You don't have permission to get another user's account");
        }
        User user = userService.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(summary = "Add User")
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "User added successfully",
                                        content = { @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = User.class)) }),
                            @ApiResponse(responseCode = "400", description = "Invalid User at creation",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))}),
                            @ApiResponse(responseCode = "401", description = "Unauthorized",
                                        content = @Content),
                            @ApiResponse(responseCode = "403", description = "Forbidden without permission",
                                        content = @Content),
                            @ApiResponse(responseCode = "422", description = "Duplicate User",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))})
    })
    @PostMapping
    public ResponseEntity<User> addUser(HttpServletRequest request, @Valid @RequestBody User user) {
        if (!request.isUserInRole(UserRole.ADMIN.name())
            && user.getRoles().contains(UserRole.ADMIN.name())){
            throw new AccessDeniedException("You don't have permission to add an admin account");
        }
        User createdUser = userService.addUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @Operation(summary = "Update User")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "User updated successfully",
                                        content = { @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = User.class)) }),
                            @ApiResponse(responseCode = "400", description = "Invalid User at update",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))}),
                            @ApiResponse(responseCode = "401", description = "Unauthorized",
                                        content = @Content),
                            @ApiResponse(responseCode = "403", description = "Forbidden without permission",
                                        content = @Content),
                            @ApiResponse(responseCode = "404", description = "User not found",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))}),
                            @ApiResponse(responseCode = "422", description = "Duplicate User",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))})
    })
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(HttpServletRequest request, @PathVariable long id, @Valid @RequestBody User newUser) {
        User currentUser = userService.getCurrentUser(request)
            .orElseThrow(() -> new AccessDeniedException("You must be logged in to modify your account"));
        if (!request.isUserInRole(UserRole.ADMIN.name())
            && currentUser.getId() != id) {
            throw new AccessDeniedException("You don't have permission to update another user's account");
        }
        User updatedUser = userService.updateUser(id, newUser);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @Operation(summary = "Delete User")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "User deleted successfully (No Content)"),
                            @ApiResponse(responseCode = "401", description = "Unauthorized",
                                        content = @Content),
                            @ApiResponse(responseCode = "403", description = "Forbidden without permission",
                                        content = @Content),
                            @ApiResponse(responseCode = "404", description = "User not found",
                                        content = {@Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorInfo.class))})
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
