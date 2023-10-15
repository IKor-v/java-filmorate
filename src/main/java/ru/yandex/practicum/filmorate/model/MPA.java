package ru.yandex.practicum.filmorate.model;

import lombok.Data;


@Data
public class MPA {
    private Integer id;
    private String name;

    public MPA() {
        this.id = 0;
        this.name = "";
    }

    public MPA(Integer id) {
        this.id = id;
        this.name = "";
    }

    public MPA(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}