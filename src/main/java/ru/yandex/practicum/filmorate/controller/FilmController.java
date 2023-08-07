package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
public class FilmController {

    private final String pathUrl = "/films";
    private final List<Film> films = new ArrayList<>();

    @GetMapping(pathUrl)
    public Collection<Film> getAllFilms() {
        return films;
    }

    @PostMapping(pathUrl)
    public Film createFilm(@RequestBody Film film) throws ValidationException {
        if (Film.validationFilm(film)) {
            film.setId(Film.getLastId());
            films.add(film);
            log.info("Добавлени фильм:" + film.getName() + ", с id = " + film.getId());
            return film;
        }
        log.info("Не удалось добавить фильм:" + film.toString());
        return null;
    }

    @PutMapping(pathUrl)
    public Film updateFilm(@RequestBody Film film) throws ValidationException {
        if (Film.validationFilm(film)) {
            int id = film.getId();
            Film oldFilm = returnFilm(id);
            if (oldFilm != null) {
                films.remove(oldFilm);
                films.add(film);
                log.info("Данные фильма '" + film.getName() + "' с id = " + film.getId() + "  обновленны.");
                return film;
            } else {
                throw new ValidationException("Фильма с id =" + id + " не найденно.");
            }
        }
        log.info("Не удалось обновить данные фильма:" + film.toString());
        return null;
    }


    private Film returnFilm(int id) {
        for (Film film : films) {
            if (film.getId() == id) {
                return film;
            }
        }
        return null;
    }
}
