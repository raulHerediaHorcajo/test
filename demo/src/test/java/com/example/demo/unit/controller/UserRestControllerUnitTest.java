package com.example.demo.unit.controller;

import com.example.demo.controller.UserRestController;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.criteria.UserCriteria;
import com.example.demo.security.config.SecurityExpressions.UserRole;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRestControllerUnitTest {

    private UserRestController userRestController;

    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userRestController = new UserRestController(userService);
    }

    @Test
    void testGetUsers() {
        Pageable pageable = PageRequest.of(0, 20);
        List<User> users = List.of(mock(User.class), mock(User.class), mock(User.class));
        Page<User> page = new PageImpl<>(users, pageable, 3);
        UserCriteria filters = mock(UserCriteria.class);
        when(userService.findAll(filters, pageable)).thenReturn(page);

        ResponseEntity<Page<User>> result = userRestController.getUsers(filters, pageable);

        verify(userService).findAll(filters, pageable);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getNumberOfElements()).isEqualTo(3);
        assertThat(result.getBody().getContent()).containsAll(users);
        assertThat(result.getBody().getPageable()).isEqualTo(pageable);
    }

    @Test
    void whenGetUserByIdWithoutLogin_thenShouldGiveAccessDeniedException() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(userService.getCurrentUser(request)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userRestController.getUser(request, 1))
            .isInstanceOf(AccessDeniedException.class)
            .hasMessageContaining("You must be logged in to access the account");

        verify(userService).getCurrentUser(request);
        verify(request, never()).isUserInRole(UserRole.ADMIN.name());
        verify(userService, never()).findById(1);
    }

    @Test
    void whenGetAnotherUserByIdWithUserRole_thenShouldGiveAccessDeniedException() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        User currentUser = mock(User.class);
        when(userService.getCurrentUser(request)).thenReturn(Optional.of(currentUser));
        when(request.isUserInRole(UserRole.ADMIN.name())).thenReturn(false);
        when(currentUser.getId()).thenReturn(2L);

        assertThatThrownBy(() -> userRestController.getUser(request, 1))
            .isInstanceOf(AccessDeniedException.class)
            .hasMessageContaining("You don't have permission to get another user's account");

        verify(userService).getCurrentUser(request);
        verify(request).isUserInRole(UserRole.ADMIN.name());
        verify(currentUser).getId();
        verify(userService, never()).findById(1);
    }

    @Test
    void whenGetNotExistUserById_thenShouldGiveUserNotFoundException() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        User currentUser = mock(User.class);
        when(userService.getCurrentUser(request)).thenReturn(Optional.of(currentUser));
        when(request.isUserInRole(UserRole.ADMIN.name())).thenReturn(true);
        when(userService.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userRestController.getUser(request, 1))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("User 1 not found");

        verify(userService).getCurrentUser(request);
        verify(request).isUserInRole(UserRole.ADMIN.name());
        verify(currentUser, never()).getId();
        verify(userService).findById(1);
    }

    @Test
    void testGetUserById() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        User currentUser = mock(User.class);
        when(userService.getCurrentUser(request)).thenReturn(Optional.of(currentUser));
        when(request.isUserInRole(UserRole.ADMIN.name())).thenReturn(false);
        when(currentUser.getId()).thenReturn(1L);
        when(userService.findById(1)).thenReturn(Optional.of(currentUser));

        ResponseEntity<User> result = userRestController.getUser(request, 1);

        verify(userService).getCurrentUser(request);
        verify(request).isUserInRole(UserRole.ADMIN.name());
        verify(currentUser).getId();
        verify(userService).findById(1);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(currentUser);
    }

    @Test
    void whenAddAdminUserWithUserRole_thenShouldGiveAccessDeniedException() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.isUserInRole(UserRole.ADMIN.name())).thenReturn(false);
        User newUser = mock(User.class);
        List<String> roles = mock(List.class);
        when(newUser.getRoles()).thenReturn(roles);
        when(roles.contains(UserRole.ADMIN.name())).thenReturn(true);

        assertThatThrownBy(() -> userRestController.addUser(request, newUser))
            .isInstanceOf(AccessDeniedException.class)
            .hasMessageContaining("You don't have permission to add an admin account");

        verify(request).isUserInRole(UserRole.ADMIN.name());
        verify(newUser).getRoles();
        verify(roles).contains(UserRole.ADMIN.name());
        verify(userService, never()).addUser(newUser);
    }

    @Test
    void testAddUser() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.isUserInRole(UserRole.ADMIN.name())).thenReturn(true);
        User newUser = mock(User.class);
        when(userService.addUser(newUser)).thenReturn(newUser);

        ResponseEntity<User> result = userRestController.addUser(request, newUser);

        verify(request).isUserInRole(UserRole.ADMIN.name());
        verify(newUser, never()).getRoles();
        verify(userService).addUser(newUser);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(newUser);
    }

    @Test
    void whenUpdateUserWithoutLogin_thenShouldGiveAccessDeniedException() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(userService.getCurrentUser(request)).thenReturn(Optional.empty());
        User user = mock(User.class);

        assertThatThrownBy(() -> userRestController.updateUser(request, 1, user))
            .isInstanceOf(AccessDeniedException.class)
            .hasMessageContaining("You must be logged in to modify your account");

        verify(userService).getCurrentUser(request);
        verify(request, never()).isUserInRole(UserRole.ADMIN.name());
        verify(userService, never()).updateUser(1, user);
    }

    @Test
    void whenUpdateAnotherUserWithUserRole_thenShouldGiveAccessDeniedException() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        User currentUser = mock(User.class);
        when(userService.getCurrentUser(request)).thenReturn(Optional.of(currentUser));
        when(request.isUserInRole(UserRole.ADMIN.name())).thenReturn(false);
        when(currentUser.getId()).thenReturn(2L);
        User user = mock(User.class);

        assertThatThrownBy(() -> userRestController.updateUser(request, 1, user))
            .isInstanceOf(AccessDeniedException.class)
            .hasMessageContaining("You don't have permission to update another user's account");

        verify(userService).getCurrentUser(request);
        verify(request).isUserInRole(UserRole.ADMIN.name());
        verify(currentUser).getId();
        verify(userService, never()).updateUser(1, user);
    }

    @Test
    void testUpdateUser() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        User currentUser = mock(User.class);
        when(userService.getCurrentUser(request)).thenReturn(Optional.of(currentUser));
        when(request.isUserInRole(UserRole.ADMIN.name())).thenReturn(true);
        User user = mock(User.class);
        when(userService.updateUser(1, user)).thenReturn(user);

        ResponseEntity<User> result = userRestController.updateUser(request, 1, user);

        verify(userService).getCurrentUser(request);
        verify(request).isUserInRole(UserRole.ADMIN.name());
        verify(currentUser, never()).getId();
        verify(userService).updateUser(1, user);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(user);
    }

    @Test
    void testDeleteUser() {
        ResponseEntity<Void> result = userRestController.deleteUser(1);

        verify(userService).deleteUser(1);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
