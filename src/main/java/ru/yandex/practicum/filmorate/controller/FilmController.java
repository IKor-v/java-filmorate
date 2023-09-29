package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@Slf4j
@RequestMapping(value = "/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }


    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) throws ValidationException {
        Film newFilm = filmService.createFilm(film);
        log.info("Добавлен фильм:" + film.getName() + ", с id = " + film.getId());
        return newFilm;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException, NotFoundException {
        Film updateFilm = filmService.updateFilm(film);
        log.info("Данные фильма '" + film.getName() + "' с id = " + film.getId() + "  обновленны.");
        return updateFilm;
    }

    @GetMapping(value = "/{filmId}")
    public Film getFilmForId(@PathVariable int filmId) {
        return filmService.getFilm(filmId);
    }

    @PutMapping(value = "/{filmId}/like/{userId}")
    public Film likeForFilm(@PathVariable long filmId, @PathVariable long userId) {   //лайк фильму

        Film result = filmService.addLike(userId, filmId);
        log.info("Пользователь с id = " + userId + " поставил лайк фильму с id =" + filmId);
        return result;
    }

    @DeleteMapping(value = "/{filmId}/like/{userId}")
    public Film delLikeForFilm(@PathVariable long filmId, @PathVariable long userId) {

        Film result = filmService.deleteLike(userId, filmId);
        log.info("Пользователь с id = " + userId + " убрал свой лайк фильму с id =" + filmId);
        return result;
    }

    @GetMapping("/popular") //?count={count}
    public Collection<Film> getPopularFilm(@RequestParam(required = false) Integer count) {
        if (count == null) {
            count = 10;
        }
        if (count < 1) {
            throw new ValidationException("Список популярных фильмов может имель тольпо положительное поличество фильмов.");
        }
        return filmService.getPopularFilms(count);
    }

}
