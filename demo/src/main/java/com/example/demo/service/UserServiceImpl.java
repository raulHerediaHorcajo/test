package com.example.demo.service;


import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.criteria.UserCriteria;
import com.example.demo.repository.specification.UserSpecification;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<User> findAll(UserCriteria filters, Pageable pageable) {
        Specification<User> specification = new UserSpecification(filters);
        return userRepository.findAll(specification, pageable);
    }

    @Override
    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

    @Override
    public User addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(long id, User newUser) {
        User oldUser = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        newUser.setPassword(newUser.getPassword().equals(oldUser.getPassword())
                ? oldUser.getPassword()
                : passwordEncoder.encode(newUser.getPassword())
        );
        newUser.setId(oldUser.getId());
        newUser.setRoles(oldUser.getRoles());
        return userRepository.save(newUser);
    }

    @Override
    public void deleteUser(long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(user);
    }

    @Override
    public Optional<User> getCurrentUser(HttpServletRequest request) {
        Principal userPrincipal = request.getUserPrincipal();
        if (userPrincipal == null) return Optional.empty();
        String email = userPrincipal.getName();
        return userRepository.findByEmail(email);
    }
}
