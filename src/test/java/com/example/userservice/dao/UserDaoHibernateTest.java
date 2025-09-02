package com.example.userservice.dao;

import com.example.userservice.exception.DaoException;
import com.example.userservice.model.User;
import com.example.userservice.util.HibernateUtil;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDaoHibernateTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("user_service")
            .withUsername("user_service_user")
            .withPassword("password");

    private static SessionFactory sessionFactory;
    private UserDaoHibernate userDao;

    @BeforeAll
    static void setUpContainer() {
        System.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        System.setProperty("hibernate.connection.username", postgres.getUsername());
        System.setProperty("hibernate.connection.password", postgres.getPassword());
        System.setProperty("hibernate.hbm2ddl.auto", "create-drop");

        sessionFactory = HibernateUtil.getSessionFactory();
    }

    @AfterAll
    static void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @BeforeEach
    void setUp() {
        userDao = new UserDaoHibernate();
        try (var session = sessionFactory.openSession()) {
            var tx = session.beginTransaction();
            session.createNativeQuery("TRUNCATE TABLE users RESTART IDENTITY").executeUpdate();
            tx.commit();
        }
    }

    @Test
    void create_validUser_persistsAndReturnsUser() {
        User user = new User("John Doe", "john@example.com", 30);

        User result = userDao.create(user);

        assertNotNull(result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals(30, result.getAge());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    void create_duplicateEmail_throwsDaoException() {
        User user1 = new User("John", "john@example.com", 30);
        userDao.create(user1);

        User user2 = new User("Jane", "john@example.com", 25);

        DaoException exception = assertThrows(DaoException.class, () -> userDao.create(user2));
        assertTrue(exception.getMessage().contains("Email must be unique"));
    }

    @Test
    void findById_existingId_returnsUser() {
        User user = new User("John Doe", "john@example.com", 30);
        user = userDao.create(user);

        Optional<User> result = userDao.findById(user.getId());

        assertTrue(result.isPresent());
        assertEquals(user.getId(), result.get().getId());
        assertEquals("John Doe", result.get().getName());
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        Optional<User> result = userDao.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void findAll_multipleUsers_returnsAllUsers() {
        User user1 = new User("John", "john@example.com", 30);
        User user2 = new User("Jane", "jane@example.com", 25);
        userDao.create(user1);
        userDao.create(user2);

        List<User> users = userDao.findAll();

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("john@example.com")));
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("jane@example.com")));
    }

    @Test
    void findAll_emptyDatabase_returnsEmptyList() {
        List<User> users = userDao.findAll();

        assertTrue(users.isEmpty());
    }

    @Test
    void update_existingUser_updatesFields() {
        User user = new User("John", "john@example.com", 30);
        user = userDao.create(user);

        user.setName("Jane");
        user.setEmail("jane@example.com");
        user.setAge(31);
        User updated = userDao.update(user);

        assertEquals(user.getId(), updated.getId());
        assertEquals("Jane", updated.getName());
        assertEquals("jane@example.com", updated.getEmail());
        assertEquals(31, updated.getAge());
    }

    @Test
    void update_duplicateEmail_throwsDaoException() {
        User user1 = new User("John", "john@example.com", 30);
        User user2 = new User("Jane", "jane@example.com", 25);
        userDao.create(user1);
        userDao.create(user2);

        user2.setEmail("john@example.com");

        DaoException exception = assertThrows(DaoException.class, () -> userDao.update(user2));
        assertTrue(exception.getMessage().contains("Email must be unique"));
    }

    @Test
    void deleteById_existingId_deletesAndReturnsTrue() {
        User user = new User("John", "john@example.com", 30);
        user = userDao.create(user);

        boolean result = userDao.deleteById(user.getId());

        assertTrue(result);
        assertFalse(userDao.findById(user.getId()).isPresent());
    }

    @Test
    void deleteById_nonExistingId_returnsFalse() {
        boolean result = userDao.deleteById(999L);

        assertFalse(result);
    }
}