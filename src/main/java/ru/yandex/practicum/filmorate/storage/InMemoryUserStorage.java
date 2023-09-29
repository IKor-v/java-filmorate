package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

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
        user.setId(getLastId());
        users.put(user.getId(), userPreparation(user));
        return user;
    }

    public User updateUser(User user) {
        long id = user.getId();
        if (users.containsKey(id)) {
            users.put(id, userPreparation(user));
            return user;
        } else {
            throw new NotFoundException("Пользователь с id = " + id + " не найден.");
        }
    }

    private long getLastId() {
        return lastId++;
    }


    public User getUser(long userId) {
        User user = users.get(userId);
        if (user != null) {
            return user;
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
