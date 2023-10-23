package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final JdbcTemplate jdbcTemplate;

    public Genre getGenreForId(int id) {
        SqlRowSet genreRow = jdbcTemplate.queryForRowSet("SELECT * FROM genre_list WHERE genre_id = ?", id);
        if (genreRow.next()) {
            return new Genre(genreRow.getInt("genre_id"), genreRow.getString("genre_name"));
        } else {
            throw new NotFoundException("Жанра с id = " + id + " не обнаружено.");
        }

    }

    public List<Genre> getAllGenres() {

        SqlRowSet genreRow = jdbcTemplate.queryForRowSet("SELECT * FROM genre_list");
        List<Genre> result = new ArrayList<>();
        while (genreRow.next()) {
            result.add(new Genre(genreRow.getInt("genre_id"), genreRow.getString("genre_name")));
        }

        return result;
    }
}
