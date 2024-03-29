package com.example.demo.integration.user;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.criteria.UserCriteria;
import com.example.demo.security.config.SecurityExpressions.UserRole;
import com.example.demo.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class UserServiceImplIntegrationTest {

    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @SpyBean
    private PasswordEncoder passwordEncoder;

    @ParameterizedTest(name = "{0}")
    @MethodSource("scenarios")
    void testFindAll(String scenario, UserCriteria filters, List<User> expectedUsers, String retrieveSql) {
        List<User> users = List.of(
            new User(
                "Test User 1",
                "test1@gmail.com",
                "ZXhhbXBsZSBwYXNzd29yZA==",
                List.of(UserRole.ADMIN.name())
            ),
            new User(
                "Test User 2",
                "test2@gmail.com",
                "ZXhhbXBsZSBwYXNzd29yZA==",
                List.of(UserRole.USER.name())
            ),
            new User(
                "Test User 3",
                "test3@gmail.com",
                "ZXhhbXBsZSBwYXNzd29yZA==",
                List.of(UserRole.USER.name())
            )
        );
        userRepository.saveAll(users);

        Pageable pageable = PageRequest.of(0, 20);
        Page<User> result = userServiceImpl.findAll(filters, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getNumberOfElements()).isEqualTo(expectedUsers.size());
        assertThat(result.getContent())
            .containsAll(expectedUsers);
        assertThat(result.getPageable()).isEqualTo(pageable);

        List<User> retrievedUsers = jdbcTemplate.query(retrieveSql, JdbcTemplateMapperFactory
            .newInstance()
            .addKeys("id")
            .newResultSetExtractor(User.class));
        assertThat(retrievedUsers).containsAll(expectedUsers);
    }

    private static Stream<Arguments> scenarios() {
        return Stream.of(
            arguments("FindAll without filters",
                new UserCriteria(null, null, null),
                List.of(
                    new User(1,
                        "Test User 1",
                        "test1@gmail.com",
                        "ZXhhbXBsZSBwYXNzd29yZA==",
                        List.of(UserRole.ADMIN.name())
                    ),
                    new User(2,
                        "Test User 2",
                        "test2@gmail.com",
                        "ZXhhbXBsZSBwYXNzd29yZA==",
                        List.of(UserRole.USER.name())
                    ),
                    new User(3,
                        "Test User 3",
                        "test3@gmail.com",
                        "ZXhhbXBsZSBwYXNzd29yZA==",
                        List.of(UserRole.USER.name())
                    )
                ),
                """
                    SELECT U.id, U.name, U.email, U.password, UR.roles
                    FROM user U
                    JOIN user_roles UR ON U.id = UR.user_id"""
            ),
            arguments("FindAll with String filter",
                new UserCriteria("Test User 1", null, null),
                List.of(
                    new User(1,
                        "Test User 1",
                        "test1@gmail.com",
                        "ZXhhbXBsZSBwYXNzd29yZA==",
                        List.of(UserRole.ADMIN.name())
                    )
                ),
                """
                    SELECT U.id, U.name, U.email, U.password, UR.roles
                    FROM user U
                    JOIN user_roles UR ON U.id = UR.user_id
                    WHERE name = 'Test User 1'"""
            ),
            arguments("FindAll with Partial String and Case Insensitivity filter",
                new UserCriteria(null, "TEst2", null),
                List.of(
                    new User(2,
                        "Test User 2",
                        "test2@gmail.com",
                        "ZXhhbXBsZSBwYXNzd29yZA==",
                        List.of(UserRole.USER.name())
                    )
                ),
                """
                    SELECT U.id, U.name, U.email, U.password, UR.roles
                    FROM user U
                    JOIN user_roles UR ON U.id = UR.user_id
                    WHERE email LIKE '%TEst2%'"""
            ),
            arguments("FindAll with Roles filter",
                new UserCriteria(null, null, List.of(UserRole.USER.name())),
                List.of(
                    new User(2,
                        "Test User 2",
                        "test2@gmail.com",
                        "ZXhhbXBsZSBwYXNzd29yZA==",
                        List.of(UserRole.USER.name())
                    ),
                    new User(3,
                        "Test User 3",
                        "test3@gmail.com",
                        "ZXhhbXBsZSBwYXNzd29yZA==",
                        List.of(UserRole.USER.name())
                    )
                ),
                """
                    SELECT U.id, U.name, U.email, U.password, UR.roles
                    FROM user U
                    JOIN user_roles UR ON U.id = UR.user_id
                    WHERE UR.roles = 'USER'"""
            ),
            arguments("FindAll with all filters",
                new UserCriteria("Test User 1", "test1@gmail.com", List.of(UserRole.ADMIN.name())),
                List.of(
                    new User(1,
                        "Test User 1",
                        "test1@gmail.com",
                        "ZXhhbXBsZSBwYXNzd29yZA==",
                        List.of(UserRole.ADMIN.name())
                    )
                ),
                """
                    SELECT U.id, U.name, U.email, U.password, UR.roles
                    FROM user U
                    JOIN user_roles UR ON U.id = UR.user_id
                    WHERE U.name = 'Test User 1'
                      AND U.email = 'test1@gmail.com'
                      AND UR.roles = 'ADMIN'"""
            ),
            arguments("FindAll with unmatched filters",
                new UserCriteria("Test User 1", "test2@gmail.com", List.of(UserRole.ADMIN.name())),
                new ArrayList<>(),
                """
                    SELECT U.id, U.name, U.email, U.password, UR.roles
                    FROM user U
                    JOIN user_roles UR ON U.id = UR.user_id
                    WHERE U.name = 'Test User 1'
                      AND U.email = 'test2@gmail.com'
                      AND UR.roles = 'ADMIN'"""
            )
        );
    }

    @Test
    void testFindById() {
        when(passwordEncoder.encode("example password")).thenReturn("ZXhhbXBsZSBwYXNzd29yZA==");
        User storedUser = userServiceImpl.addUser(
            new User(
            "Test User",
            "test@gmail.com",
            "example password",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name()))
        );

        Optional<User> resultUser = userServiceImpl.findById(storedUser.getId());

        User expectedUser = new User(1,
            "Test User",
            "test@gmail.com",
            "ZXhhbXBsZSBwYXNzd29yZA==",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name())
        );
        assertThat(resultUser)
            .isPresent()
            .contains((expectedUser));

        Optional<User> retrievedUser = userRepository.findById(resultUser.get().getId());
        assertThat(retrievedUser)
            .isPresent()
            .contains((expectedUser));
    }

    @Test
    void testAddUser() {
        when(passwordEncoder.encode("example password")).thenReturn("ZXhhbXBsZSBwYXNzd29yZA==");
        User user = new User(
            "Test User",
            "test@gmail.com",
            "example password",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name())
        );

        User resultUser = userServiceImpl.addUser(user);

        User expectedUser = new User(1,
            "Test User",
            "test@gmail.com",
            "ZXhhbXBsZSBwYXNzd29yZA==",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name())
        );
        assertThat(resultUser).isEqualTo(expectedUser);

        Optional<User> retrievedUser = userRepository.findById(resultUser.getId());
        assertThat(retrievedUser)
            .isPresent()
            .contains((expectedUser));
    }

    @Test
    void testUpdateUser() {
        when(passwordEncoder.encode("example password")).thenReturn("ZXhhbXBsZSBwYXNzd29yZA==");
        User storedUser = userServiceImpl.addUser(new User(
            "Test User",
            "test@gmail.com",
            "example password",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name()))
        );

        User newUser = new User(
            "New test User",
            "newtest@gmail.com",
            "ZXhhbXBsZSBwYXNzd29yZA==",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name())
        );

        User resultUser = userServiceImpl.updateUser(storedUser.getId(), newUser);

        User expectedUser = new User(1,
            "New test User",
            "newtest@gmail.com",
            "ZXhhbXBsZSBwYXNzd29yZA==",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name())
        );
        assertThat(resultUser).isEqualTo(expectedUser);

        Optional<User> retrievedUser = userRepository.findById(resultUser.getId());
        assertThat(retrievedUser)
            .isPresent()
            .contains((expectedUser));
    }

    @Test
    void testDeleteUser() {
        User storedUser = userServiceImpl.addUser(new User(
            "Test User",
            "test@gmail.com",
            "example password",
            List.of(UserRole.ADMIN.name(), UserRole.USER.name()))
        );

        userServiceImpl.deleteUser(storedUser.getId());

        Optional<User> retrievedUser = userRepository.findById(storedUser.getId());
        assertThat(retrievedUser).isEmpty();
    }
}