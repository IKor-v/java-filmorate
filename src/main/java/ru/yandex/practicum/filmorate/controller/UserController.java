package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private final String pathUrl = "/users";


    @GetMapping(value = pathUrl)
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping(value = pathUrl)
    public User createUser(@RequestBody User user) throws ValidationException {
        if (User.validationUser(user)) {
            user.setId(User.getLastId());
            users.put(user.getId(), user);
            log.info("Добавлен пользователь с id = " + user.getId());
            return user;
        }
        log.info("Не удалось добавить пользователя: " + user.toString());
        return null;
    }

    @PutMapping(value = pathUrl)
    public User updateUser(@RequestBody User user) throws ValidationException {
        if (User.validationUser(user)) {
            int id = user.getId();
            if (users.containsKey(id)) {
                users.put(id, user);
                log.info("Данные пользователя с id = " + id + " обновленны.");
                return user;
            } else {
                throw new NotFoundException("Пользователь с id = " + id + " не найден.");
            }
        }
        log.info("Не удалось обновить данные пользователя: " + user.toString());
        return null;
    }


}
