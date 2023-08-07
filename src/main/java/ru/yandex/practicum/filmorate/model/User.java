package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.controller.ValidationException;

import java.time.LocalDate;

@Data
public class User {
    private static int lastId = 1;
    private int id; //идентификатор
    private String email; //почта
    private String login; //логин
    private String name; //имя
    private LocalDate birthday; //день рождения

    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email.trim();
        this.login = login.trim();
        if ((name == null) || (name.isBlank())) {
            this.name = login.trim();
        } else {
            this.name = name.trim();
        }
        this.birthday = birthday;
    }

    public static int getLastId() {
        return lastId++;
    }

    public static boolean validationUser(User user) throws ValidationException {
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
}
