package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Genre {
    private int id = 0;
    private String name = "";

    public Genre() {
    }

    public Genre(int id) {
        this.id = id;
        this.name = "";
    }

    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
