package com.example.userservice.service;

import com.example.userservice.dao.UserDao;
import com.example.userservice.exception.DaoException;
import com.example.userservice.model.User;

import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUser(String name, String email, Integer age) {
        if (name == null || name.isBlank() || email == null || email.isBlank()) {
            throw new IllegalArgumentException("Name and email must not be empty");
        }
        User user = new User(name, email, age);
        return userDao.create(user);
    }

    public Optional<User> getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID");
        }
        return userDao.findById(id);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public User updateUser(Long id, String name, String email, Integer age) {
        Optional<User> existing = userDao.findById(id);
        if (existing.isEmpty()) {
            throw new DaoException("User not found with id: " + id);
        }
        User user = existing.get();
        if (name != null && !name.isBlank()) {
            user.setName(name);
        }
        if (email != null && !email.isBlank()) {
            user.setEmail(email);
        }
        if (age != null) {
            user.setAge(age);
        }
        return userDao.update(user);
    }

    public boolean deleteUser(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID");
        }
        return userDao.deleteById(id);
    }
}
