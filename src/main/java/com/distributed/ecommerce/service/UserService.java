package com.distributed.ecommerce.service;

import com.distributed.ecommerce.config.DatabaseConfig;
import com.distributed.ecommerce.entity.User;
import com.distributed.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#id")
    public Optional<User> findById(Long id) {
        DatabaseConfig.DatabaseContextHolder.setDbType("slave");
        try {
            return userRepository.findById(id);
        } finally {
            DatabaseConfig.DatabaseContextHolder.clearDbType();
        }
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#email")
    public Optional<User> findByEmail(String email) {
        DatabaseConfig.DatabaseContextHolder.setDbType("slave");
        try {
            return userRepository.findByEmail(email);
        } finally {
            DatabaseConfig.DatabaseContextHolder.clearDbType();
        }
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public User save(User user) {
        DatabaseConfig.DatabaseContextHolder.setDbType("master");
        try {
            return userRepository.save(user);
        } finally {
            DatabaseConfig.DatabaseContextHolder.clearDbType();
        }
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        DatabaseConfig.DatabaseContextHolder.setDbType("slave");
        try {
            return userRepository.findAll();
        } finally {
            DatabaseConfig.DatabaseContextHolder.clearDbType();
        }
    }

    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deleteById(Long id) {
        DatabaseConfig.DatabaseContextHolder.setDbType("master");
        try {
            userRepository.deleteById(id);
        } finally {
            DatabaseConfig.DatabaseContextHolder.clearDbType();
        }
    }

    public boolean existsByEmail(String email) {
        DatabaseConfig.DatabaseContextHolder.setDbType("slave");
        try {
            return userRepository.existsByEmail(email);
        } finally {
            DatabaseConfig.DatabaseContextHolder.clearDbType();
        }
    }
}