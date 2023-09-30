package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class FilmService {  //добавление и удаление лайка, вывод 10 наиболее популярных фильмов по количеству лайков
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film createFilm(Film film) {
        if (!validationFilm(film)) {
            throw new ValidationException("Не удалось добавить фильм: " + film.toString());
        }
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        if (!validationFilm(film)) {
            throw new ValidationException("Не удалось обновить фильм: " + film.toString());
        }
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(long filmId) {
        return filmStorage.getFilm(filmId);
    }

    public Film addLike(long userId, long filmId) {
        Film film = filmStorage.getFilm(filmId);
        if (film == null) {
            throw new NotFoundException("Фильма с тами id не обнаруженно.");
        }
        if (!(checkUser(userId))) {
            throw new NotFoundException("Пользователя с тами id не обнаруженно.");
        }
        List<Long> likeList = film.getLikeList();
        if (!likeList.contains(userId)) {
            likeList.add(userId);
            film.setLikeList(likeList);
            filmStorage.updateFilm(film);
        }
        return film;

    }

    public Film deleteLike(long userId, long filmId) {
        Film film = filmStorage.getFilm(filmId);
        if (film == null) {
            throw new NotFoundException("Фильма с тами id не обнаруженно.");
        }
        if (!(checkUser(userId))) {
            throw new NotFoundException("Пользователя с тами id не обнаруженно.");
        }

        List<Long> likeList = film.getLikeList();
        if (likeList.contains(userId)) {
            likeList.remove(userId);
            film.setLikeList(likeList);
            filmStorage.updateFilm(film);
        }
        return film;

    }


    public Collection<Film> getPopularFilms(int count) {
        Collection<Film> result = new ArrayList<>();
        Film minimalFilm = new Film(0, "1", "", LocalDate.now(), 1);
        int minimalLikeInList = -1;

        if (filmStorage.getSize() == 0) {
            return result;
        }
        if (filmStorage.getSize() < count) {
            count = (int) filmStorage.getSize();
        }

        for (Film film : filmStorage.getAllFilms()) {
            int numberLikeForFilm = film.getLikeList().size();
            if (result.size() == 0) {
                minimalLikeInList = numberLikeForFilm;
                minimalFilm = film;
                result.add(film);
                continue;
            }
            if (result.size() < count) {
                if (numberLikeForFilm < minimalLikeInList) {
                    minimalFilm = film;
                    minimalLikeInList = numberLikeForFilm;
                }
                result.add(film);
            } else if ((numberLikeForFilm > minimalLikeInList) && (result.size() >= count)) {
                result.remove(minimalFilm);
                result.add(film);
            }


        }
        return result;

    }


    private boolean checkUser(long userId) {
        if (userService.getUser(userId) != null) {
            return true;
        }
        return false;
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
