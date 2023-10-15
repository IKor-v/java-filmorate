package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Film {
    private long id;
    @NotBlank
    private String name;
    @Size(min = 0, max = 200, message = "Длинна описания не может быть больше 200 символов.")
    private String description;
    @NotNull
    private LocalDate releaseDate;

    @NotNull
    @Positive
    private int duration;

/*    @ToString.Exclude
    private Duration fullDuration;*/

    private MPA mpa = new MPA();

    private List<Genre> genres = new ArrayList<>();

    private List<Long> likeList = new ArrayList<>();

    public Film(String name, String description, LocalDate releaseDate, int duration, MPA mpa) {
        this.id = 0;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public Film(long id, String name, String description, LocalDate releaseDate, int duration, MPA mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public Film(long id, String name, String description, LocalDate releaseDate, int duration, MPA mpa, List<Genre> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = genres;
    }

    public Film(String name, String description, LocalDate releaseDate, int duration, MPA mpa, List<Genre> genres) {
        this.id = 0;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = genres;
    }


    public Film(long id, String name, String description, LocalDate releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
