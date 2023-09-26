package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.NotFoundException;
import ru.yandex.practicum.filmorate.controller.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {  //хранение, обновление и поиск
    private final Map<Long, Film> films = new HashMap<>();
    private int lastId = 1;


    public Collection<Film> getAllFilms() {
        return films.values();
    }

    public long getSize() {
        return films.size();
    }

    public Film createFilm(Film film) {
        if (validationFilm(film)) {
            film.setId(getLastId());
            films.put(film.getId(), film);
            return film;
        }
        throw new ValidationException("Не удалось добавить фильм: " + film.toString());
    }

    public Film updateFilm(Film film) {
        if (validationFilm(film)) {
            long id = film.getId();
            if (films.containsKey(id)) {
                films.put(id, film);
                return film;
            } else {
                throw new NotFoundException("Фильма с id =" + id + " не найденно.");
            }
        }
        throw new ValidationException("Не удалось обновить данные фильма:" + film.toString());
    }

    public Film getFilm(long filmId) {
        if (films.containsKey(filmId)) {
            return films.get(filmId);
        } else {
            throw new NotFoundException("Фильма с id =" + filmId + " не найденно."); //??
        }
    }

    private int getLastId() {
        return lastId++;
    }

    private boolean validationFilm(Film film) throws ValidationException {
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
