package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {  //добавление, удаление и модификация фильмов

    public Collection<Film> getAllFilms();
    public Film createFilm(Film film);
    public Film updateFilm(Film film);
    public Film getFilm (long filmId);
    public long getSize();

}
