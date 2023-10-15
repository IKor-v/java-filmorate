package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.ArrayList;
import java.util.List;

@Service
public class MpaService {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public MPA getMpaForId(int id) {
        SqlRowSet mpaRow = jdbcTemplate.queryForRowSet("SELECT * FROM mpa_list WHERE mpa_id = ?", id);
        if (mpaRow.next()) {
            return new MPA(mpaRow.getInt("mpa_id"), mpaRow.getString("mpa_name"));
        } else {
            throw new NotFoundException("Жанра с id = " + id + " не обнаружено.");
        }
    }

    public List<MPA> getAllMpa( ){

        SqlRowSet mpaRow = jdbcTemplate.queryForRowSet("SELECT * FROM mpa_list");
        List<MPA> result = new ArrayList<>();
        while (mpaRow.next()) {
            result.add( new MPA(mpaRow.getInt("mpa_id"), mpaRow.getString("mpa_name")));
        }
        return result;
    }

}
