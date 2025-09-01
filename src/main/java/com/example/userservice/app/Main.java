package com.example.userservice.app;

import com.example.userservice.dao.UserDao;
import com.example.userservice.dao.UserDaoHibernate;
import com.example.userservice.exception.DaoException;
import com.example.userservice.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final UserDao userDao = new UserDaoHibernate();

    public static void main(String[] args) {
        log.info("User Service started");
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu();
                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1" -> create(scanner);
                    case "2" -> readById(scanner);
                    case "3" -> readAll();
                    case "4" -> update(scanner);
                    case "5" -> delete(scanner);
                    case "0" -> {
                        running = false;
                        System.out.println("Bye!");
                    }
                    default -> System.out.println("Unknown option. Try again.");
                }
            }
        } catch (Exception e) {
            log.error("Fatal error: ", e);
        }
        log.info("User Service stopped");
    }

    private static void printMenu() {
        System.out.println("\n=== USER SERVICE ===");
        System.out.println("1. Create user");
        System.out.println("2. Read user by id");
        System.out.println("3. Read all users");
        System.out.println("4. Update user");
        System.out.println("5. Delete user");
        System.out.println("0. Exit");
        System.out.print("Select: ");
    }

    private static void create(Scanner sc) {
        try {
            System.out.print("Name: ");
            String name = sc.nextLine().trim();

            System.out.print("Email: ");
            String email = sc.nextLine().trim();

            System.out.print("Age (empty if unknown): ");
            String ageStr = sc.nextLine().trim();
            Integer age = ageStr.isEmpty() ? null : Integer.parseInt(ageStr);

            User created = userDao.create(new User(name, email, age));
            System.out.println("Created: " + created);
        } catch (NumberFormatException e) {
            System.out.println("Age must be a number.");
        } catch (DaoException e) {
            System.out.println("Create failed: " + e.getMessage());
        }
    }

    private static void readById(Scanner sc) {
        try {
            System.out.print("ID: ");
            Long id = Long.parseLong(sc.nextLine().trim());
            Optional<User> user = userDao.findById(id);
            System.out.println(user.map(Object::toString).orElse("Not found"));
        } catch (NumberFormatException e) {
            System.out.println("ID must be a number.");
        } catch (DaoException e) {
            System.out.println("Read failed: " + e.getMessage());
        }
    }

    private static void readAll() {
        try {
            List<User> users = userDao.findAll();
            if (users.isEmpty()) System.out.println("No users yet.");
            else users.forEach(System.out::println);
        } catch (DaoException e) {
            System.out.println("Read all failed: " + e.getMessage());
        }
    }

    private static void update(Scanner sc) {
        try {
            System.out.print("ID to update: ");
            Long id = Long.parseLong(sc.nextLine().trim());

            Optional<User> existing = userDao.findById(id);
            if (existing.isEmpty()) {
                System.out.println("User not found.");
                return;
            }
            User u = existing.get();

            System.out.print("New name (enter to keep '" + u.getName() + "'): ");
            String name = sc.nextLine().trim();
            if (!name.isEmpty()) u.setName(name);

            System.out.print("New email (enter to keep '" + u.getEmail() + "'): ");
            String email = sc.nextLine().trim();
            if (!email.isEmpty()) u.setEmail(email);

            System.out.print("New age (enter to keep " + u.getAge() + "): ");
            String ageStr = sc.nextLine().trim();
            if (!ageStr.isEmpty()) u.setAge(Integer.parseInt(ageStr));

            User updated = userDao.update(u);
            System.out.println("Updated: " + updated);
        } catch (NumberFormatException e) {
            System.out.println("Age/ID must be a number.");
        } catch (DaoException e) {
            System.out.println("Update failed: " + e.getMessage());
        }
    }

    private static void delete(Scanner sc) {
        try {
            System.out.print("ID to delete: ");
            Long id = Long.parseLong(sc.nextLine().trim());
            boolean ok = userDao.deleteById(id);
            System.out.println(ok ? "Deleted." : "User not found.");
        } catch (NumberFormatException e) {
            System.out.println("ID must be a number.");
        } catch (DaoException e) {
            System.out.println("Delete failed: " + e.getMessage());
        }
    }
}
