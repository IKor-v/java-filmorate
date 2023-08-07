package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping(value = "/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private int lastId = 1;

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (validationFilm(film)) {
            film.setId(getLastId());
            films.put(film.getId(), film);
            log.info("Добавлен фильм:" + film.getName() + ", с id = " + film.getId());
            return film;
        }
        throw new ValidationException("Не удалось добавить фильм: " + film.toString());
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException, NotFoundException {
        if (validationFilm(film)) {
            int id = film.getId();
            if (films.containsKey(id)) {
                films.put(id, film);
                log.info("Данные фильма '" + film.getName() + "' с id = " + film.getId() + "  обновленны.");
                return film;
            } else {
                throw new NotFoundException("Фильма с id =" + id + " не найденно.");
            }
        }
        throw new ValidationException("Не удалось обновить данные фильма:" + film.toString());
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
