package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) throws ValidationException {
        User newUser = userService.createUser(user);
        log.info("Добавлен пользователь с id = " + user.getId());
        return newUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        User updateUser = userService.updateUser(user);
        log.info("Данные пользователя с id = " + updateUser.getId() + " обновленны.");
        return updateUser;
    }

    @GetMapping("/{userId}")
    public User getUserForId(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.addFriend(id, friendId);
        log.info("Пользователь с id = " + id + " подружился с пользователем с id = " + friendId);
        return userService.getUser(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User delFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.deleteFriend(id, friendId);
        log.info("Пользователь с id = " + id + " перестал дружить с пользователем с id = " + friendId);
        return userService.getUser(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@PathVariable long id) {
        return userService.getAllFriends(id);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public List<User> getCommonFriend(@PathVariable long userId, @PathVariable long otherId) {
        return userService.getCommonFriends(userId, otherId);
    }

}
