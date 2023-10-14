package ru.yandex.practicum.filmorate.storage;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

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

        film.setId(getLastId());
        films.put(film.getId(), film);
        return film;

    }

    public Film updateFilm(Film film) {
        long id = film.getId();
        if (films.containsKey(id)) {
            films.put(id, film);
            return film;
        } else {
            throw new NotFoundException("Фильма с id =" + id + " не найденно.");
        }

    }

    public Film getFilm(long filmId) {
        Film film = films.get(filmId);
        if (film != null) {
            return film;
        } else {
            throw new NotFoundException("Фильма с id =" + filmId + " не найденно."); //??
        }
    }


    private int getLastId() {
        return lastId++;
    }

}
