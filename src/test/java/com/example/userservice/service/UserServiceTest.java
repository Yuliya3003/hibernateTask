package com.example.userservice.service;

import com.example.userservice.dao.UserDao;
import com.example.userservice.exception.DaoException;
import com.example.userservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("John Doe", "john@example.com", 30);
        user.setId(1L);
    }

    @Test
    void createUser_validUser_returnsCreatedUser() {
        when(userDao.create(any(User.class))).thenReturn(user);

        User result = userService.createUser("John Doe", "john@example.com", 30);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals(30, result.getAge());
        verify(userDao).create(any(User.class));
    }

    @Test
    void createUser_duplicateEmail_throwsDaoException() {
        when(userDao.create(any(User.class))).thenThrow(new DaoException("Email must be unique"));

        DaoException exception = assertThrows(DaoException.class, () ->
                userService.createUser("John Doe", "john@example.com", 30));

        assertEquals("Email must be unique", exception.getMessage());
        verify(userDao).create(any(User.class));
    }

    @Test
    void getUserById_existingId_returnsUser() {
        when(userDao.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userDao).findById(1L);
    }

    @Test
    void getUserById_nonExistingId_returnsEmpty() {
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(1L);

        assertFalse(result.isPresent());
        verify(userDao).findById(1L);
    }

    @Test
    void getAllUsers_returnsUserList() {
        List<User> users = List.of(user);
        when(userDao.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals(user, result.get(0));
        verify(userDao).findAll();
    }

    @Test
    void updateUser_existingUser_returnsUpdatedUser() {
        when(userDao.findById(1L)).thenReturn(Optional.of(user));
        when(userDao.update(any(User.class))).thenReturn(user);

        User result = userService.updateUser(1L, "Jane Doe", "jane@example.com", 31);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getName());
        assertEquals("jane@example.com", result.getEmail());
        assertEquals(31, result.getAge());
        verify(userDao).findById(1L);
        verify(userDao).update(any(User.class));
    }

    @Test
    void updateUser_nonExistingUser_throwsDaoException() {
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        DaoException exception = assertThrows(DaoException.class, () ->
                userService.updateUser(1L, "Jane Doe", "jane@example.com", 31));

        assertEquals("User not found with id: 1", exception.getMessage());
        verify(userDao).findById(1L);
        verify(userDao, never()).update(any(User.class));
    }

    @Test
    void deleteUser_existingId_returnsTrue() {
        when(userDao.deleteById(1L)).thenReturn(true);

        boolean result = userService.deleteUser(1L);

        assertTrue(result);
        verify(userDao).deleteById(1L);
    }

    @Test
    void deleteUser_nonExistingId_returnsFalse() {
        when(userDao.deleteById(1L)).thenReturn(false);

        boolean result = userService.deleteUser(1L);

        assertFalse(result);
        verify(userDao).deleteById(1L);
    }
}
