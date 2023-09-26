package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@Slf4j
@RequestMapping(value = "/users")
public class UserController {
   UserStorage userStorage;
   UserService userService;

   @Autowired
   public UserController (UserStorage userStorage, UserService userService){
       this.userStorage = userStorage;
       this.userService = userService;
   }

    @GetMapping
    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) throws ValidationException {
        User newUser = userStorage.createUser(user);
        userService.addUserInList(user.getId());
        log.info("Добавлен пользователь с id = " + user.getId());
        return newUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        User updateUser = userStorage.updateUser(user);
        log.info("Данные пользователя с id = " + updateUser.getId() + " обновленны.");
        return updateUser;
    }

    @GetMapping("/{userId}")
    public User getUserForId (@PathVariable long userId) {
        return userStorage.getUser(userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend (@PathVariable long id, @PathVariable long friendId) {
        userService.addFriend(id, friendId);
        log.info("Пользователь с id = " + id + " подружился с пользователем с id = " + friendId);
        return userStorage.getUser(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User delFriend (@PathVariable long id, @PathVariable long friendId) {
        userService.deleteFriend(id, friendId);
        log.info("Пользователь с id = " + id + " перестал дружить с пользователем с id = " + friendId);
        return userStorage.getUser(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriends (@PathVariable long id) {
       return userService.getAllFriends(id);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public List<User> getCommonFriend (@PathVariable long userId, @PathVariable long otherId){
       return userService.getCommonFriends(userId, otherId);
    }

}
