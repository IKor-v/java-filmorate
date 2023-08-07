package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping(value = "/users")
public class UserController {
    private int lastId = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) throws ValidationException {
        if (validationUser(user)) {
            user.setId(getLastId());
            users.put(user.getId(), userPreparation(user));
            log.info("Добавлен пользователь с id = " + user.getId());
            return user;
        }
        throw new ValidationException("Не удалось добавить пользователя: " + user.toString());
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        if (validationUser(user)) {
            int id = user.getId();
            if (users.containsKey(id)) {
                users.put(id, userPreparation(user));
                log.info("Данные пользователя с id = " + id + " обновленны.");
                return user;
            } else {
                throw new NotFoundException("Пользователь с id = " + id + " не найден.");
            }
        }
        throw new ValidationException("Не удалось обновить данные пользователя: " + user.toString());
    }

    private int getLastId() {
        return lastId++;
    }

    private boolean validationUser(User user) throws ValidationException {
        String message = "Ошибка валидации пользователя: ";
        if (user == null) {
            message += "переданно пустое тело.";
            throw new ValidationException(message);
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            message += "дата рождения не может быть в будущем.";
        } else if ((user.getEmail().isBlank()) || !(user.getEmail().contains("@"))) {
            message += "адрес электронной почты не может быть пустым или без '@'.";
        } else if ((user.getLogin()).isBlank() || (user.getLogin().contains(" "))) {
            message += "логин не может быть пустым или содержать пробелы";
        } else {
            return true;
        }
        throw new ValidationException(message);
    }

    private User userPreparation(User user) {
        if ((user.getName() == null) || (user.getName().isBlank())) {
            user.setName(user.getLogin());
        }
        user.setName(user.getName().trim());
        user.setLogin(user.getLogin().trim());
        user.setEmail(user.getEmail().trim());

        return user;
    }

}
