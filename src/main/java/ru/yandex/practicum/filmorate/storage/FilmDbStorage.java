package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@Primary
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film getFilm(long filmId) {
        SqlRowSet filmRow = jdbcTemplate.queryForRowSet("SELECT * FROM films LEFT OUTER JOIN mpa_list ON films.mpa_id = mpa_list.mpa_id WHERE film_id = ?", filmId); //
        if (filmRow.next()) {
            Film film = new Film(
                    filmRow.getInt("film_id"),
                    filmRow.getString("film_name"),
                    filmRow.getString("description"),
                    filmRow.getDate("release_date").toLocalDate(),
                    filmRow.getInt("duration")
                    , new MPA(filmRow.getInt("mpa_id"), filmRow.getString("mpa_name"))
            );
            film.setGenres(getGenreForFilmId(filmId));
            film.setLikeList(getLikeListForFilmId(filmId));
            return film;
        } else {
            throw new NotFoundException("Не найдено фильма с id = " + filmId);
        }
    }


    @Override
    public Collection<Film> getAllFilms() {
        SqlRowSet filmRow = jdbcTemplate.queryForRowSet("SELECT * FROM films LEFT OUTER JOIN mpa_list ON films.mpa_id = mpa_list.mpa_id ORDER BY film_id");
        Collection<Film> result = new ArrayList<>();

        while (filmRow.next()) {
            Film film = new Film(
                    filmRow.getInt("film_id"),
                    filmRow.getString("film_name"),
                    filmRow.getString("description"),
                    filmRow.getDate("release_date").toLocalDate(),
                    filmRow.getInt("duration")
                    , new MPA(filmRow.getInt("mpa_id"), filmRow.getString("mpa_name")));
            long id = film.getId();
            film.setGenres(getGenreForFilmId(id));
            film.setLikeList(getLikeListForFilmId(id));
            result.add(film);
        }
        return result;
    }

    @Override
    public Film createFilm(Film film) {
        String requestSQL = "INSERT INTO films (film_name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(requestSQL, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        film.setId(getIdInLastCreate(film));
        film.setGenres(updateGenres(film));

        return updateLikeList(film);
    }


    @Override
    public Film updateFilm(Film film) {
        getFilm(film.getId());
        String requestUsersSQL = "UPDATE films SET film_name= ?, description= ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";

        jdbcTemplate.update(requestUsersSQL,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        film.setGenres(updateGenres(film));
        return updateLikeList(film);
    }


    @Override
    public long getSize() {
        SqlRowSet filmRow = jdbcTemplate.queryForRowSet("SELECT COUNT(film_id) AS count FROM films");
        long result = 0;
        if (filmRow.next()) {
            result = filmRow.getInt("count");
        } else {
            throw new NotFoundException("Таблица 'films' не обнаружена.");
        }
        return result;
    }

    private List<Genre> updateGenres(Film film) {
        if (film == null) {
            throw new NotFoundException("Передан пустой фильм.");
        }
        long filmId = film.getId();
        List<Integer> list = new ArrayList<>();
        List<Genre> result = film.getGenres();
        jdbcTemplate.update("DELETE FROM genres WHERE film_id = ?", filmId);
        String requestGenreSQL = "INSERT INTO genres (film_id, genre_id) VALUES (?, ?)";

        for (Genre genre : result) {
            int id = genre.getId();
            if (!list.contains(id)) {
                list.add(genre.getId());
                jdbcTemplate.update(requestGenreSQL, filmId, id);
            }
        }
        return getGenreForFilmId(filmId);
    }

    private List<Long> getLikeListForFilmId(long filmId) {
        SqlRowSet likeListRow = jdbcTemplate.queryForRowSet("SELECT user_id FROM like_list WHERE film_id = ?", filmId);
        List<Long> likeList = new ArrayList<>();
        while (likeListRow.next()) {
            likeList.add((long) likeListRow.getInt("user_id"));
        }

        return likeList;
    }

    private List<Genre> getGenreForFilmId(long filmId) {
        SqlRowSet genreRow = jdbcTemplate.queryForRowSet("SELECT * FROM genres LEFT OUTER JOIN genre_list ON genres.genre_id = genre_list.genre_id WHERE film_id = ?", filmId);
        List<Genre> result = new ArrayList<>();
        while (genreRow.next()) {
            result.add(new Genre(genreRow.getInt("genre_id"), genreRow.getString("genre_name")));
        }
        return result;
    }

    private Film updateLikeList(Film film) {
        long filmId = getIdInLastCreate(film);
        film.setId(filmId);
        List<Long> newLikeList = film.getLikeList();

        if ((newLikeList != null) && (newLikeList.size() > 0)) {
            String requestLikeListSQL = "INSERT INTO like_list (film_id, user_id) VALUES(?, ?)";  // MERGE INTO like_list KEY (film_id) VALUES(?, ?)
            for (Long userId : newLikeList) {
                jdbcTemplate.update(requestLikeListSQL, filmId, userId);
            }
        }
        return film;
    }

    private long getIdInLastCreate(Film film) {
        SqlRowSet filmRow = jdbcTemplate.queryForRowSet(
                "SELECT film_id FROM films WHERE film_name = ? AND description = ? AND release_date = ? AND duration = ? ORDER BY film_id DESC",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
        filmRow.next();

        return filmRow.getInt("film_id");
    }


}
