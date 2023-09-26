package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.NotFoundException;
import ru.yandex.practicum.filmorate.controller.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private int lastId = 1;
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public User createUser(User user) {
        if (validationUser(user)) {
            user.setId(getLastId());
            users.put(user.getId(), userPreparation(user));

            return user;
        }
        throw new ValidationException("Не удалось добавить пользователя: " + user.toString());
    }

    public User updateUser(User user) {
        if (validationUser(user)) {
            long id = user.getId();
            if (users.containsKey(id)) {
                users.put(id, userPreparation(user));
                return user;
            } else {
                throw new NotFoundException("Пользователь с id = " + id + " не найден.");
            }
        }
        throw new ValidationException("Не удалось обновить данные пользователя: " + user.toString());
    }

    private long getLastId() {
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

    public User getUser(long userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            throw new NotFoundException("Такого пользователя нет"); //??
        }
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
