package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "/films")
public class FilmController {

    //FilmStorage filmStorage = new InMemoryFilmStorage();

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController (FilmStorage filmStorage, FilmService filmService ) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }


    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) throws ValidationException {
        Film newFilm =  filmStorage.createFilm(film);
        filmService.addFilmInList(newFilm.getId());
        log.info("Добавлен фильм:" + film.getName() + ", с id = " + film.getId());
        return newFilm;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException, NotFoundException {
        Film updateFilm =  filmStorage.updateFilm(film);
        //filmService.addFilmInList(updateFilm.getId());
        log.info("Данные фильма '" + film.getName() + "' с id = " + film.getId() + "  обновленны.");
        return updateFilm;
    }

    @GetMapping(value = "/{filmId}")
    public Film getFilmForId (@PathVariable int filmId) {
        return  filmStorage.getFilm(filmId);
    }

    @PutMapping(value = "/{filmId}/like/{userId}")
    public Film likeForFilm (@PathVariable long filmId, @PathVariable long userId) {   //лайк фильму

        Film result = filmService.addLike(userId, filmId);
        log.info("Пользователь с id = " + userId + " поставил лайк фильму с id =" + filmId);
        return result;
    }

    @DeleteMapping(value = "/{filmId}/like/{userId}")
    public Film delLikeForFilm (@PathVariable long filmId, @PathVariable long userId) {

        Film result = filmService.deleteLike(userId, filmId);
        log.info("Пользователь с id = " + userId + " убрал свой лайк фильму с id =" + filmId);
        return result;
    }

    @GetMapping("/popular") //?count={count}
    public Collection<Film>  getPopularFilm (@RequestParam(required = false) Integer count) {
        if (count == null) {
            count = 10;
        }
        if (count < 1) {
            throw new ValidationException("Список популярных фильмов может имель тольпо положительное поличество фильмов.");
        }
        return filmService.getPopularFilms(count);
    }

}
