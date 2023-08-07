package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.ToString;
import ru.yandex.practicum.filmorate.controller.ValidationException;

import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    private static int lastId = 1;
    int id;  //идентификатор
    String name; //имя
    String description;
    LocalDate releaseDate; //дата релиза
    int duration; //продолжительность фильма

    @ToString.Exclude
    Duration fullDuration;

    public Film(int id, String name, String description, LocalDate releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.fullDuration = Duration.ofMinutes(duration);
    }


    public static int getLastId() {
        return lastId++;
    }

    public static boolean validationFilm(Film film) throws ValidationException {
        String message = "Ошибка валидации фильма: ";
        if (film == null) {
            message += "переданно пустое тело.";
            throw new ValidationException(message);
        }
        if (film.getName().isBlank()) {
            message += "название фильма не может быть пустым.";
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 25))) {
            message += "дата выхода фильма не может быть раньше 25.12.1895";
        } else if (film.getDescription().length() > 200) {
            message += "длинна описания не может быть более 200 символов.";
        } else if (film.getFullDuration().toSeconds() <= 0) {
            message += "продолжительность фильма может быть только положительной.";
        } else {
            return true;
        }
        throw new ValidationException(message);
    }

}
