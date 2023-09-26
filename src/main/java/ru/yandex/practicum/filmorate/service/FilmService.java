package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.NotFoundException;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Service
public class FilmService {  //добавление и удаление лайка, вывод 10 наиболее популярных фильмов по количеству лайков
    private final Map<Long, Set<Long>> filmLikeList = new HashMap<>(); //id фильма и список id пользователей

    private final FilmStorage filmStorage;

    @Autowired
    UserController userController;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addLike(long userId, long filmId) {
        if (!checkFilm(filmId) || !(checkUser(userId))) {
            throw new NotFoundException("Фильма с тами id не обнаруженно.");
        }
        Set<Long> result;
        if (!filmLikeList.containsKey(filmId)) {
            result = new HashSet<>();
        } else {
            result = filmLikeList.get(filmId);
        }
        result.add(userId);
        filmLikeList.put(filmId, result);
        return filmStorage.getFilm(filmId);
    }

    public Film deleteLike(long userId, long filmId) {
        if (!checkFilm(filmId) || !(checkUser(userId)) || (!filmLikeList.containsKey(filmId))) {
            throw new NotFoundException("Фильма с таким id не обнаруженно.");
        }
        Set<Long> result;

        result = filmLikeList.get(filmId);
        result.remove(userId);
        filmLikeList.put(filmId, result);
        return filmStorage.getFilm(filmId);

    }

    public Collection<Film> getPopularFilms(int count) {
        Collection<Film> result = new ArrayList<>();
        Map<Long, Integer> resultList = new HashMap<>(); // id и количество лайков
        long idFilmInMinimal = 0;
        int minimalLikeInList = -1;

        if (filmStorage.getSize() == 0) {
            return result;
        }
        if (filmStorage.getSize() < count) {
            count = (int) filmStorage.getSize();
        }
        for (Map.Entry<Long, Set<Long>> film : filmLikeList.entrySet()) {
            if (resultList.size() == 0) {
                resultList.put(film.getKey(), film.getValue().size());
                idFilmInMinimal = film.getKey();
                minimalLikeInList = film.getValue().size();
                continue;
            }

            if ((film.getValue().size() > minimalLikeInList) && (resultList.size() >= count)) {
                resultList.remove(idFilmInMinimal);
            }
            resultList.put(film.getKey(), film.getValue().size());


            idFilmInMinimal = getIdMin(resultList);
            minimalLikeInList = filmLikeList.get(idFilmInMinimal).size();
        }

        for (Map.Entry idAndLike : resultList.entrySet()) { // сопоставление id к Film
            result.add(filmStorage.getFilm((Long) idAndLike.getKey()));
        }
        return result;
    }


    public void addFilmInList(long filmId) {
        if (!filmLikeList.containsKey(filmId)) {
            filmLikeList.put(filmId, new HashSet<>());
        }
    }

    private long getIdMin(Map<Long, Integer> intList) {
        long result = 0;
        int min = -1;
        for (Map.Entry<Long, Integer> i : intList.entrySet()) {
            if ((min == -1) && (min != i.getValue())) {
                min = i.getValue();
                result = i.getKey();
                continue;
            }
            if (i.getValue() < min) {
                min = i.getValue();
                result = i.getKey();
            }
        }
        return result;
    }

    private boolean checkUser(long userId) {
        if (userController.getUserForId(userId) != null) {
            return true;
        }
        return false;
    }

    private boolean checkFilm(long filmId) {
        if (filmStorage.getFilm((int) filmId) != null) {
            return true;
        }
        return false;
    }
}
