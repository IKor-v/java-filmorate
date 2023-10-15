package ru.yandex.practicum.filmorate.storage;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Primary
@Component
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User getUser(long userId) {
        SqlRowSet userRow = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id = ?", userId);

        if (userRow.next()) {
            User user = new User(
                    userRow.getInt("user_id"),
                    userRow.getString("email"),
                    userRow.getString("login"),
                    userRow.getString("user_name"),
                    userRow.getDate("birthday").toLocalDate());
            user.setFriendList(getFriendListForId(userId));
            return user;
        } else {
            throw new NotFoundException("Такой пользователь не найден");
        }
    }


    @Override
    public Collection<User> getAllUsers() {
        SqlRowSet userRow = jdbcTemplate.queryForRowSet("SELECT * FROM users ORDER BY user_id");
        Collection<User> result = new ArrayList<>();

        while (userRow.next()) {
            User user = new User(
                    userRow.getInt("user_id"),
                    userRow.getString("email"),
                    userRow.getString("login"),
                    userRow.getString("user_name"),
                    userRow.getDate("birthday").toLocalDate()
            );
            user.setFriendList(getFriendListForId(user.getId()));
            result.add(user);
        }
        return result;
    }

    @Override
    public User createUser(User user) {
        String requestUsersSQL = "INSERT INTO users (login, user_name, email, birthday) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(requestUsersSQL, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday());

        user.setId(getIdInLastUser(user));
        return user;
    }

    @Override
    public User updateUser(User user) {
        getUser(user.getId());
        String requestSQL = "UPDATE users SET login = ?, user_name = ?, email = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(requestSQL, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday(), user.getId());

        return updateFriendList(user);
    }

    private User updateFriendList(User user) {
        String request1SQL = "DELETE FROM friend_list WHERE user_id = ?";
        jdbcTemplate.update(request1SQL, user.getId());

        String requestSQL = "INSERT INTO friend_list (user_id, friend_id) VALUES (?, ?)";
        for (Long friendId : user.getFriendList()) {
            jdbcTemplate.update(requestSQL, user.getId(), friendId);
        }
        return user;
    }

    private long getIdInLastUser(User user) {
        SqlRowSet userRow = jdbcTemplate.queryForRowSet(
                "SELECT user_id FROM users WHERE login = ? AND user_name = ? AND email = ? AND birthday = ? ORDER BY user_id DESC",
                user.getLogin(), user.getName(), user.getEmail(), user.getBirthday());
        userRow.next();

        return userRow.getInt("user_id");
    }

    private List<Long> getFriendListForId(long userId) {
        List<Long> result = new ArrayList<>();

        SqlRowSet requestFriendRow = jdbcTemplate.queryForRowSet(
                "SELECT friend_id FROM friend_list WHERE user_id = ?", userId);
        while (requestFriendRow.next()) {
            result.add((long) requestFriendRow.getInt("friend_id"));
        }
        return result;
    }


}
