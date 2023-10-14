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
                    userRow.getString("name"),
                    userRow.getDate("birthday").toLocalDate()
            );

            user.setFriendList(getFriendListForId(userId));
            //user.setRequestFriendList(getRequestFriendForId(userId));
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
                    userRow.getString("name"),
                    userRow.getDate("birthday").toLocalDate()
            );

            user.setFriendList(getFriendListForId(user.getId()));
            result.add(user);

        }
        return result;
    }

    @Override
    public User createUser(User user) {
        String requestUsersSQL = "INSERT INTO users (login, name, email, birthday) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(requestUsersSQL, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday());

        user.setId(getIdInLastUser(user));
        return user;
    }

    @Override
    public User updateUser(User user) {
        getUser(user.getId());
        String requestSQL = "UPDATE users SET login = ?, name = ?, email = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(requestSQL, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday(), user.getId());

        return updateFriendList(user);
    }

    private User updateFriendList(User user) {
        //if (deleteFriendListIfNeedUpdate(user))
/*        String requestSQL = "DELETE FROM friend_list WHERE user_id = ?";
        jdbcTemplate.update(requestSQL, user.getId());*/

            long user_id = user.getId();
            String requestSQL = "INSERT INTO friend_list (user_id, friend_id) VALUES (?, ?)"; //ON CONFLICT DO NOTHING;
            for (Long friendId : user.getFriendList()) {
                jdbcTemplate.update(requestSQL, user_id, friendId);
            }

        return user;
    }

    @Override
    public boolean addFriendListForID(long senderId, long recipientId) {

        String requestSQL = "INSERT INTO friend_list (user_id, friend_id) VALUES (?, ?)"; //ON CONFLICT DO NOTHING;
        jdbcTemplate.update(requestSQL, senderId, recipientId);
        return true;
    }

    @Override
    public boolean removeFriendListForID(long senderId, long recipientId) {

        String requestSQL = "DELETE FROM friend_list WHERE user_id = ? AND friend_id = ?"; //ON CONFLICT DO NOTHING;
        jdbcTemplate.update(requestSQL, senderId, recipientId);
        return true;
    }

    private boolean deleteFriendListIfNeedUpdate(User user) {
        User oldUser = getUser(user.getId());
        List<Long> userFriendList = user.getFriendList();
        List<Long> oldUserFriendList = oldUser.getFriendList();
        if (userFriendList.size() != oldUserFriendList.size()) {
            String requestSQL = "DELETE FROM like_list WHERE user_id = ?";
            jdbcTemplate.update(requestSQL, user.getId());
            return true;
        }
        if (!userFriendList.containsAll(oldUserFriendList)) {
            String requestSQL = "DELETE FROM like_list WHERE user_id = ?";
            jdbcTemplate.update(requestSQL, user.getId());
            return true;
        }

        return false;
    }


    private long getIdInLastUser(User user) {
        SqlRowSet userRow = jdbcTemplate.queryForRowSet(
                "SELECT user_id FROM users WHERE login = ? AND name = ? AND email = ? AND birthday = ? ORDER BY user_id DESC",
                user.getLogin(), user.getName(), user.getEmail(), user.getBirthday());
        userRow.next();

        return userRow.getInt("user_id");
    }

/*    private List<Long> getRequestFriendForId (long userId) {
        List<Long> result = new ArrayList<>();

        SqlRowSet requestFriendRow = jdbcTemplate.queryForRowSet(
                "SELECT recipient_id FROM requests_friend_list WHERE sender_id =?", userId);
        while (requestFriendRow.next()) {
            result.add((long) requestFriendRow.getInt("recipient_id"));
        }

        return null;
    }*/

    private List<Long> getFriendListForId(long userId) {
        List<Long> result = new ArrayList<>();

/*        SqlRowSet requestFriendRow = jdbcTemplate.queryForRowSet(
                "SELECT sender_id FROM requests_friend_list WHERE recipient_id =?"
                , userId);
        while (requestFriendRow.next()) {
            result.add((long) requestFriendRow.getInt("sender_id"));
        }*/

/*        requestFriendRow = jdbcTemplate.queryForRowSet(
                "SELECT friend2_id FROM friend_list WHERE friend1_id = ?", userId);
        while (requestFriendRow.next()) {
            result.add((long) requestFriendRow.getInt("friend2_id"));
        }*/

        SqlRowSet requestFriendRow = jdbcTemplate.queryForRowSet(
                "SELECT user_id FROM friend_list WHERE friend_id = ?", userId);
        while (requestFriendRow.next()) {
            result.add((long) requestFriendRow.getInt("user_id"));
        }

        return result;
    }


}
