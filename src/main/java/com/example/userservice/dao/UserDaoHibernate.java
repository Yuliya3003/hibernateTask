package com.example.userservice.dao;

import com.example.userservice.exception.DaoException;
import com.example.userservice.model.User;
import com.example.userservice.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserDaoHibernate implements UserDao {
    private static final Logger log = LoggerFactory.getLogger(UserDaoHibernate.class);

    @Override
    public User create(User user) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
            log.info("Created user id={}", user.getId());
            return user;
        } catch (ConstraintViolationException e) {
            rollbackQuietly(tx);
            throw new DaoException("Email must be unique: " + user.getEmail(), e);
        } catch (Exception e) {
            rollbackQuietly(tx);
            throw new DaoException("Failed to create user", e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(User.class, id));
        } catch (Exception e) {
            throw new DaoException("Failed to read user by id=" + id, e);
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from User", User.class).list();
        } catch (Exception e) {
            throw new DaoException("Failed to read all users", e);
        }
    }

    @Override
    public User update(User user) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(user);
            tx.commit();
            log.info("Updated user id={}", user.getId());
            return user;
        } catch (ConstraintViolationException e) {
            rollbackQuietly(tx);
            throw new DaoException("Email must be unique: " + user.getEmail(), e);
        } catch (Exception e) {
            rollbackQuietly(tx);
            throw new DaoException("Failed to update user id=" + user.getId(), e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            User managed = session.get(User.class, id);
            if (managed == null) {
                tx.rollback();
                return false;
            }
            session.remove(managed);
            tx.commit();
            log.info("Deleted user id={}", id);
            return true;
        } catch (Exception e) {
            rollbackQuietly(tx);
            throw new DaoException("Failed to delete user id=" + id, e);
        }
    }

    private void rollbackQuietly(Transaction tx) {
        if (tx != null) try { tx.rollback(); } catch (Exception ignored) {}
    }
}

