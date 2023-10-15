package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

class UserControllerTest {
    private UserController userController;

    @BeforeEach
    void setup() {
        //userController = new UserController(new UserService(new InMemoryUserStorage()));
    }

    @Test
    void createAndShowUsers() throws ValidationException {
        Collection<User> users = userController.getAllUsers();
        Assertions.assertNotNull(users);
        Assertions.assertEquals(0, users.size());
        userController.createUser(new User(1, "kor@mail.ru", "car", "Man", LocalDate.now().minusYears(20)));
        users = userController.getAllUsers();
        Assertions.assertEquals(1, users.size());
    }

    @Test
    void createUserIfError() {
        int countError = 0;
        try {
            userController.createUser(new User(1, "kor@mail.ru", "", "Man", LocalDate.now().minusYears(20)));
        } catch (ValidationException e) {
            countError++;
        }

        try {
            userController.createUser(new User(2, "kor@mail.ru", " ", "Man", LocalDate.now().minusYears(20)));
        } catch (ValidationException e) {
            countError++;
        }

        try {
            userController.createUser(new User(3, "kormail.ru", "car", "Man", LocalDate.now().minusYears(20)));
        } catch (ValidationException e) {
            countError++;
        }

        try {
            userController.createUser(new User(4, "", "car", "Man", LocalDate.now().minusYears(20)));
        } catch (ValidationException e) {
            countError++;
        }

        try {
            userController.createUser(new User(5, " ", "car", "Man", LocalDate.now().minusYears(20)));
        } catch (ValidationException e) {
            countError++;
        }

        try {
            userController.createUser(new User(6, "kor@mail.ru", "car", "Man", LocalDate.now().plusDays(1)));
        } catch (ValidationException e) {
            countError++;
        }

        try {
            userController.createUser(new User(7, "kor@mail.ru", "Car Man", "Man", LocalDate.now().minusYears(20)));
        } catch (ValidationException e) {
            countError++;
        }

        try {
            userController.createUser(null);
        } catch (ValidationException e) {
            countError++;
        }

        Collection<User> users = userController.getAllUsers();
        Assertions.assertEquals(0, users.size());
        Assertions.assertEquals(8, countError);

    }

    @Test
    void createUsersIfBoundaryValues() throws ValidationException {
        User user = new User(1, "kor@mail.ru", "car", "", LocalDate.now().minusYears(20));
        userController.createUser(user);
        User user1 = new User(2, "banr@mail.ru", "Jkkj", "Jack", LocalDate.now());
        userController.createUser(user1);
        Collection<User> users = userController.getAllUsers();
        Assertions.assertEquals(2, users.size());
        Assertions.assertTrue(users.contains(user));
    }

    @Test
    void updateUser() throws ValidationException {
        User user = new User(1, "kor@mail.ru", "car", "Jack", LocalDate.now().minusYears(20));
        userController.createUser(user);
        User user1 = new User(user.getId(), "korgi@mail.ru", "Cartman", "Jack", LocalDate.now().minusYears(21));
        userController.updateUser(user1);
        Collection<User> users = userController.getAllUsers();
        Assertions.assertEquals(1, users.size());
        Assertions.assertTrue(users.contains(user1));
        Assertions.assertFalse(users.contains(user));
    }

    @Test
    void updateUserIfError() {
        int countError = 0;
        User user = new User(1, "kor@mail.ru", "Car", "Man", LocalDate.now().minusYears(20));
        try {
            userController.createUser(user);
        } catch (ValidationException e) {
            countError++;
        }

        try {
            userController.updateUser(new User(1, "kor@mail.ru", "", "Man", LocalDate.now().minusYears(20)));
        } catch (ValidationException e) {
            countError++;
        }

        try {
            userController.updateUser(new User(2, "kor@mail.ru", " ", "Man", LocalDate.now().minusYears(20)));
        } catch (ValidationException e) {
            countError++;
        }

        try {
            userController.updateUser(new User(3, "kormail.ru", "car", "Man", LocalDate.now().minusYears(20)));
        } catch (ValidationException e) {
            countError++;
        }

        try {
            userController.updateUser(new User(4, "", "car", "Man", LocalDate.now().minusYears(20)));
        } catch (ValidationException e) {
            countError++;
        }

        try {
            userController.updateUser(new User(5, " ", "car", "Man", LocalDate.now().minusYears(20)));
        } catch (ValidationException e) {
            countError++;
        }

        try {
            userController.updateUser(new User(6, "kor@mail.ru", "car", "Man", LocalDate.now().plusDays(1)));
        } catch (ValidationException e) {
            countError++;
        }

        try {
            userController.updateUser(new User(7, "kor@mail.ru", "Car Man", "Man", LocalDate.now().minusYears(20)));
        } catch (ValidationException e) {
            countError++;
        }

        try {
            userController.updateUser(null);
        } catch (ValidationException e) {
            countError++;
        }

        Collection<User> users = userController.getAllUsers();
        Assertions.assertNotNull(users);
        Assertions.assertEquals(1, users.size());
        Assertions.assertTrue(users.contains(user));
        Assertions.assertEquals(8, countError);
    }

    @Test
    void getUser() {
        User user = new User(1, "banr@mail.ru", "Jkkj", "Jack", LocalDate.now());
        userController.createUser(user);
        User user1 = userController.getUserForId(user.getId());
        Assertions.assertEquals(user1, user);
    }

    @Test
    void getNotExistUser() {
        boolean check = false;
        try {
            User user1 = userController.getUserForId(1);
        } catch (NotFoundException e) {
            check = true;
        }
        Assertions.assertTrue(check);

    }

}