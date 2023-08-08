package com.example.demo.unit.service;

import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.criteria.UserCriteria;
import com.example.demo.repository.specification.UserSpecification;
import com.example.demo.security.config.SecurityExpressions.UserRole;
import com.example.demo.service.UserServiceImpl;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    private UserServiceImpl userServiceImpl;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        userServiceImpl = new UserServiceImpl(userRepository, passwordEncoder);
    }

    @Test
    void whenFindAllWithUnmatchedFilters_thenShouldGiveEmptyPage() {
        UserCriteria filters = mock(UserCriteria.class);
        Specification<User> specification = new UserSpecification(filters);
        Pageable pageable = PageRequest.of(0, 20);
        Page<User> page = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(userRepository.findAll(specification, pageable)).thenReturn(page);

        Page<User> result = userServiceImpl.findAll(filters, pageable);

        verify(userRepository).findAll(specification, pageable);
        assertThat(result).isNotNull();
        assertThat(result.getNumberOfElements()).isZero();
        assertThat(result.isEmpty()).isTrue();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getPageable()).isEqualTo(pageable);
    }

    @Test
    void testFindAll() {
        UserCriteria filters = mock(UserCriteria.class);
        Specification<User> specification = new UserSpecification(filters);
        Pageable pageable = PageRequest.of(0, 20);
        List<User> users = List.of(
            new User(1,
                "Test User",
                "test@gmail.com",
                "ZXhhbXBsZSBwYXNzd29yZA==",
                List.of(UserRole.ADMIN.name(), UserRole.USER.name())
            ),
            new User(2,
                "Distinct User",
                "other@gmail.com",
                "yYyYyYyYyYyYyYyYy==",
                List.of(UserRole.USER.name()))
        );
        Page<User> page = new PageImpl<>(users, pageable, 2);

        when(userRepository.findAll(specification, pageable)).thenReturn(page);

        Page<User> result = userServiceImpl.findAll(filters, pageable);

        verify(userRepository).findAll(specification, pageable);
        assertThat(result).isNotNull();
        assertThat(result.getNumberOfElements()).isEqualTo(2);
        assertThat(result.getContent()).containsAll(users);
        assertThat(result.getPageable()).isEqualTo(pageable);
    }

    @Test
    void whenFindByIdUserDoesNotExist_thenShouldGiveOptionalEmpty() {
        when(userRepository.findById((long) 1)).thenReturn(Optional.empty());

        Optional<User> resultUser = userServiceImpl.findById(1);

        verify(userRepository).findById((long) 1);
        assertThat(resultUser)
            .isNotPresent();
    }

    @Test
    void testFindById() {
        User expectedUser = new User(1,
            "Test User",
            "test@gmail.com",
            "ZXhhbXBsZSBwYXNzd29yZA==",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name())
        );

        when(userRepository.findById((long) 1)).thenReturn(Optional.of(expectedUser));

        Optional<User> resultUser = userServiceImpl.findById(1);

        verify(userRepository).findById((long) 1);
        assertThat(resultUser)
            .isPresent()
            .contains((expectedUser));
    }

    @Test
    void testAddUser(){
        User user = new User(
            "Test User",
            "test@gmail.com",
            "example password",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name())
        );
        User expectedUser = new User(1,
            "Test User",
            "test@gmail.com",
            "ZXhhbXBsZSBwYXNzd29yZA==",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name())
        );

        when(passwordEncoder.encode(user.getPassword())).thenReturn("ZXhhbXBsZSBwYXNzd29yZA==");
        when(userRepository.save(user)).thenReturn(expectedUser);

        User resultUser = userServiceImpl.addUser(user);

        verify(passwordEncoder).encode("example password");
        verify(userRepository).save(user);
        assertThat(resultUser).isEqualTo(expectedUser);
    }

    @Test
    void whenUpdateUserDoesNotExist_thenShouldGiveUserNotFoundException() {
        User newUser = new User(
            "New test User",
            "newtest@gmail.com",
            "new example password",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name())
        );
        when(userRepository.findById((long) 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userServiceImpl.updateUser(1, newUser))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("User 1 not found");

        verify(userRepository).findById((long) 1);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void whenUpdateUserWithSamePassword_thenShouldGiveUpdatedUserWithSamePassword() {
        User newUser = new User(
            "New test User",
            "newtest@gmail.com",
            "ZXhhbXBsZSBwYXNzd29yZA==",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name())
        );
        User storedUser = new User(1,
            "Test User",
            "test@gmail.com",
            "ZXhhbXBsZSBwYXNzd29yZA==",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name())
        );
        User expectedUser = new User(1,
            "New test User",
            "newtest@gmail.com",
            "ZXhhbXBsZSBwYXNzd29yZA==",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name())
        );
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(storedUser));
        when(userRepository.save(newUser)).thenReturn(expectedUser);

        User resultUser = userServiceImpl.updateUser(1, newUser);

        verify(userRepository).findById((long) 1);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(newUser);
        assertThat(resultUser).isEqualTo(expectedUser);
    }

    @Test
    void testUpdateUser() {
        User newUser = new User(
            "New test User",
            "newtest@gmail.com",
            "new example password",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name())
        );
        User storedUser = new User(1,
            "Test User",
            "test@gmail.com",
            "ZXhhbXBsZSBwYXNzd29yZA==",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name())
        );
        User expectedUser = new User(1,
            "New test User",
            "newtest@gmail.com",
            "bmV3IGV4YW1wbGUgcGFzc3dvcmQ==",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name())
        );
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(storedUser));
        when(passwordEncoder.encode(newUser.getPassword())).thenReturn("bmV3IGV4YW1wbGUgcGFzc3dvcmQ==");
        when(userRepository.save(newUser)).thenReturn(expectedUser);

        User resultUser = userServiceImpl.updateUser(1, newUser);

        verify(userRepository).findById((long) 1);
        verify(passwordEncoder).encode("new example password");
        verify(userRepository).save(newUser);
        assertThat(resultUser).isEqualTo(expectedUser);
    }

    @Test
    void whenDeleteUserDoesNotExist_thenShouldGiveUserNotFoundException() {
        when(userRepository.findById((long) 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userServiceImpl.deleteUser(1))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("User 1 not found");

        verify(userRepository).findById((long) 1);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void testDeleteUser() {
        User user = new User(1,
            "Test User",
            "test@gmail.com",
            "ZXhhbXBsZSBwYXNzd29yZA==",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name())
        );
        when(userRepository.findById((long) 1)).thenReturn(Optional.of(user));

        userServiceImpl.deleteUser(1);

        verify(userRepository).findById((long) 1);
        verify(userRepository).delete(user);
    }

    @Test
    void whenGetCurrentUserIsNotAuthenticated_thenShouldGiveOptionalEmpty() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getUserPrincipal()).thenReturn(null);

        Optional<User> resultUser = userServiceImpl.getCurrentUser(request);

        verify(request).getUserPrincipal();
        verify(userRepository, never()).findByEmail(anyString());
        assertThat(resultUser)
            .isNotPresent();
    }

    @Test
    void testGetCurrentUser() {
        User currentUser = new User(1,
            "Test User",
            "test@gmail.com",
            "ZXhhbXBsZSBwYXNzd29yZA==",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name())
        );
        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal userPrincipal = mock(Principal.class);
        when(request.getUserPrincipal()).thenReturn(userPrincipal);
        when(userPrincipal.getName()).thenReturn(currentUser.getEmail());
        when(userRepository.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));

        Optional<User> resultUser = userServiceImpl.getCurrentUser(request);

        verify(request).getUserPrincipal();
        verify(userPrincipal).getName();
        verify(userRepository).findByEmail(currentUser.getEmail());
        assertThat(resultUser)
            .isPresent()
            .contains((currentUser));
    }
}