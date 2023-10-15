package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class User {

    private long id;
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String login;
    private String name;
    @NotNull
    @PastOrPresent
    private LocalDate birthday;

    //@ToString.Exclude
    //private List<Long> requestFriendList = new ArrayList<>();  //id пользователей, которым мы отправили запрос
    private List<Long> friendList = new ArrayList<>(); //id пользователей, которые дружат с нами

    public User(long id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
