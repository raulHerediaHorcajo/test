package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.criteria.UserCriteria;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    Page<User> findAll(UserCriteria filters, Pageable pageable);
    Optional<User> findById(long id);
    User addUser(User user);
    User updateUser(long id, User newUser);
    void deleteUser(long id);
    Optional<User> getCurrentUser(HttpServletRequest request);
}
