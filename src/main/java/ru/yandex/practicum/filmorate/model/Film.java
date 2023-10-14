package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
//@NoArgsConstructor
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
    @ToString.Exclude
    private Duration fullDuration;

    private MPA mpa; //enum?

    private List<Integer> genre = new ArrayList<>(); //enum?

    private List<Long> likeList = new ArrayList<>();


    public Film(long id, String name, String description, LocalDate releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.fullDuration = Duration.ofMinutes(duration);
    }


    public Film(long id, String name, String description, LocalDate releaseDate, int duration,  MPA mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.fullDuration = Duration.ofMinutes(duration);
        this.mpa = mpa;
    }
    public Film(long id, String name, String description, LocalDate releaseDate, int duration, MPA mpa, List<Integer> genre, List<Long> likeList) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.fullDuration = Duration.ofMinutes(duration);
        this.mpa = mpa;
        this.genre = genre;
        this.likeList = likeList;
    }

    /*
    public Film(long id, String name, String description, LocalDate releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.fullDuration = Duration.ofMinutes(duration);
    }







    public Film(long id, String name, String description, LocalDate releaseDate, int duration, MPA mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.fullDuration = Duration.ofMinutes(duration);
        //this.mpa = mpa;
    }



    public Film(long id, String name, String description, LocalDate releaseDate, int duration, MPA mpa, List<Integer> genre, List<Long> likeList) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;

        //this.mpa = mpa;
        this.genre = genre;
        this.likeList = likeList;
    }*/
}
