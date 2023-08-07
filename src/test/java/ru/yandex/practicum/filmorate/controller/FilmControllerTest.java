package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;

@Slf4j
class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    void setup() {
        filmController = new FilmController();
    }

    @Test
    void createAndShowFilms() throws ValidationException {
        Collection<Film> films = filmController.getAllFilms();
        Assertions.assertNotNull(films);
        Assertions.assertEquals(0, films.size());
        filmController.createFilm(new Film(1, "Титаник", "Девушка не подвинулась на двери", LocalDate.now().minusYears(23), Duration.ofHours(2)));
        films = filmController.getAllFilms();
        Assertions.assertEquals(1, films.size());
    }

    @Test
    void createFilmsIfError() {
        int i = 0;
        try {
            filmController.createFilm(new Film(1, "Форест Гамп", "Беги, лес, беги! ",
                    LocalDate.now().minusYears(30), Duration.ofHours(-1)));
        } catch (ValidationException e) {
            i++;
        }

        try {
            filmController.createFilm(new Film(2, "Аватар",
                    "Синяки деруться за дерево, но коротышки в зеленом не сдаются и потом происходит всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое",
                    LocalDate.now().minusYears(13), Duration.ofHours(2)));
        } catch (ValidationException e) {
            i++;
        }

        try {
            filmController.createFilm(new Film(3, "", "Терминатор заблудился в джунглях",
                    LocalDate.now().minusYears(23), Duration.ofHours(2)));
        } catch (ValidationException e) {
            i++;
        }

        try {
            filmController.createFilm(new Film(4, " ", "Два друга смотрят на море",
                    LocalDate.now().minusYears(23), Duration.ofHours(2)));
        } catch (ValidationException e) {
            i++;
        }

        try {
            filmController.createFilm(new Film(5, "Форест Гамп", "Беги, лес, беги! ",
                    LocalDate.now().minusYears(30), Duration.ofHours(0)));
        } catch (ValidationException e) {
            i++;
        }

        try {
            filmController.createFilm(null);
        } catch (ValidationException e) {
            i++;
        }

        Collection<Film> films = filmController.getAllFilms();
        Assertions.assertEquals(0, films.size());
        Assertions.assertEquals(6, i);

    }

    @Test
    void createFilmsIfBoundaryValues() throws ValidationException {
        filmController.createFilm(new Film(1, "Форест Гамп", "Беги, лес, беги! ",
                LocalDate.now().minusYears(30), Duration.ofSeconds(1)));

        filmController.createFilm(new Film(2, "Аватар",
                "Синяки деруться за дерево, но коротышки в зеленом не сдаются и потом происходит всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-",
                LocalDate.now().minusYears(13), Duration.ofHours(2)));

        filmController.createFilm(new Film(3, "Х", "Терминатор заблудился в джунглях",
                LocalDate.now().minusYears(23), Duration.ofHours(2)));


        Collection<Film> films = filmController.getAllFilms();
        Assertions.assertEquals(3, films.size());

    }

    @Test
    void updateFilm() throws ValidationException {
        Film film = new Film(1, "Форест Гамп", "Беги, лес, беги! ",
                LocalDate.now().minusYears(30), Duration.ofHours(2));
        filmController.createFilm(film);
        Collection<Film> films = filmController.getAllFilms();
        Assertions.assertEquals(1, films.size());
        Assertions.assertTrue(films.contains(film));

        Film film1 = new Film(film.getId(), "Форрест Гамп", "Беги, лес, беги! ",
                LocalDate.now().minusYears(30), Duration.ofMinutes(144));
        filmController.updateFilm(film1);
        films = filmController.getAllFilms();
        Assertions.assertEquals(1, films.size());
        Assertions.assertTrue(films.contains(film1));
        Assertions.assertFalse(films.contains(film));
    }

    @Test
    void updateFilmIfError() throws ValidationException {
        int i = 0;
        Film film = new Film(1, "Форест Гамп", "Беги, лес, беги! ",
                LocalDate.now().minusYears(30), Duration.ofHours(2));
        filmController.createFilm(film);

        try {
            filmController.updateFilm(new Film(1, "Форест", "Беги, лес, беги! ",
                    LocalDate.now().minusYears(30), Duration.ofHours(-1)));
        } catch (ValidationException e) {
            i++;
        }

        try {
            filmController.updateFilm(new Film(1, "Аватар",
                    "Синяки деруться за дерево, но коротышки в зеленом не сдаются и потом происходит всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое-всякое",
                    LocalDate.now().minusYears(13), Duration.ofHours(2)));
        } catch (ValidationException e) {
            i++;
        }

        try {
            filmController.updateFilm(new Film(1, "", "Терминатор заблудился в джунглях",
                    LocalDate.now().minusYears(23), Duration.ofHours(2)));
        } catch (ValidationException e) {
            i++;
        }

        try {
            filmController.createFilm(new Film(1, " ", "Два друга смотрят на море",
                    LocalDate.now().minusYears(23), Duration.ofHours(2)));
        } catch (ValidationException e) {
            i++;
        }

        try {
            filmController.createFilm(new Film(1, "Форест Гамп", "Беги, лес, беги! ",
                    LocalDate.now().minusYears(30), Duration.ofHours(0)));
        } catch (ValidationException e) {
            i++;
        }

        Collection<Film> films = filmController.getAllFilms();
        Assertions.assertEquals(1, films.size());
        Assertions.assertTrue(films.contains(film));
        Assertions.assertEquals(5, i);
    }
}