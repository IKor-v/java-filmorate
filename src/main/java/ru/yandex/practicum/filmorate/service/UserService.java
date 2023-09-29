package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserService {  //добавление в друзья, удаление из друзей, вывод списка общих друзей

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User createUser(User user) {
        if (!validationUser(user)) {
            throw new ValidationException("Не удалось добавить пользователя: " + user.toString());
        }
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        if (!validationUser(user)) {
            throw new ValidationException("Не удалось обновить данные пользователя: " + user.toString());
        }
        return userStorage.updateUser(user);
    }

    public User getUser(long userId) {
        return userStorage.getUser(userId);
    }

    public void addFriend(long userId, long friendId) {
        checkUser(userId);
        checkUser(friendId);
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        List<Long> userFriendsList = user.getFriendList();
        List<Long> friendFriendsList = friend.getFriendList();

        if (!userFriendsList.contains(friendId)) {
            userFriendsList.add(friendId);
            friendFriendsList.add(userId);
            user.setFriendList(userFriendsList);
            friend.setFriendList(friendFriendsList);
            userStorage.updateUser(user);
            userStorage.updateUser(friend);
        }


    }

    public void deleteFriend(long userId, long unfriendId) {
        checkUser(userId);
        checkUser(unfriendId);
        User user = userStorage.getUser(userId);
        User unfriend = userStorage.getUser(unfriendId);
        List<Long> userFriendsList = user.getFriendList();
        List<Long> unfriendFriendsList = unfriend.getFriendList();

        if (userFriendsList.contains(unfriendId)) {
            userFriendsList.remove(unfriendId);
            unfriendFriendsList.remove(userId);
            user.setFriendList(userFriendsList);
            unfriend.setFriendList(unfriendFriendsList);
            userStorage.updateUser(user);
            userStorage.updateUser(unfriend);
        }

    }

    public List<User> getAllFriends(long userId) {
        checkUser(userId);
        List<User> result = new ArrayList<>();
        List<Long> friendList = userStorage.getUser(userId).getFriendList();
        for (Long id : friendList) {
            result.add(userStorage.getUser(id));
        }

        return result;
    }

    public List<User> getCommonFriends(long userId, long friendId) {
        checkUser(userId);
        checkUser(friendId);
        List<User> result = new ArrayList<>();
        List<Long> userFriends = userStorage.getUser(userId).getFriendList();
        List<Long> friendFriends = userStorage.getUser(friendId).getFriendList();
        if ((!userFriends.isEmpty()) && (!friendFriends.isEmpty())) {
            for (Long userFriendId : userFriends) {
                if (friendFriends.contains(userFriendId)) {
                    result.add(userStorage.getUser(userFriendId));
                }
            }

        }
        return result;
    }

    private boolean checkUser(long id) {
        if (userStorage.getUser(id) != null) {
            return true;
        }
        return false;
    }

    private boolean validationUser(User user) throws ValidationException {
        String message = "Ошибка валидации пользователя: ";
        if (user == null) {
            message += "переданно пустое тело.";
            throw new ValidationException(message);
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            message += "дата рождения не может быть в будущем.";
        } else if ((user.getEmail().isBlank()) || !(user.getEmail().contains("@"))) {
            message += "адрес электронной почты не может быть пустым или без '@'.";
        } else if ((user.getLogin()).isBlank() || (user.getLogin().contains(" "))) {
            message += "логин не может быть пустым или содержать пробелы";
        } else {
            return true;
        }
        throw new ValidationException(message);
    }
}
