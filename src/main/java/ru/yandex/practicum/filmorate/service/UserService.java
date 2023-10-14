package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
        String userName = user.getName();
        if ((userName == null) || (userName.isBlank())) {
            user.setName(user.getLogin());
        }
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        if (!validationUser(user)) {
            throw new ValidationException("Не удалось обновить данные пользователя: " + user.toString());
        }
        String userName = user.getName();
        if ((userName == null) || (userName.isBlank())) {
            user.setName(user.getLogin());
        }
        return userStorage.updateUser(user);
    }

    public User getUser(long userId) {
        return userStorage.getUser(userId);
    }



    public void addFriend(long userId, long friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        if ((user == null) || (friend == null)) {
            throw new NotFoundException("Пользователь не найден");
        }
        List<Long> userFriendsList = user.getFriendList();
        List<Long> friendFriendsList = friend.getFriendList();

        //userStorage.addFriendListForID(userId, friendId);

/*       if (!friendFriendsList.contains(userId)) {
            friendFriendsList.add(userId);
            friend.setFriendList(friendFriendsList);
            userStorage.updateUser(friend);
        }*/

        if (!userFriendsList.contains(friendId)) {
            userFriendsList.add(friendId);
            //friendFriendsList.add(userId);
            user.setFriendList(userFriendsList);
            //friend.setFriendList(friendFriendsList);
            userStorage.updateUser(user);
            //userStorage.updateUser(friend);
        }


    }

    public void deleteFriend(long userId, long unfriendId) {
        User user = userStorage.getUser(userId);
        User unfriend = userStorage.getUser(unfriendId);
        if ((user == null) || (unfriend == null)) {
            throw new NotFoundException("Пользователь не найден");
        }
        List<Long> userFriendsList = user.getFriendList();
        List<Long> unfriendFriendsList = unfriend.getFriendList();

        userStorage.removeFriendListForID(userId, unfriendId);


/*        if (unfriendFriendsList.contains(userId)) {
            unfriendFriendsList.remove(userId);
            unfriend.setFriendList(unfriendFriendsList);
            userStorage.updateUser(unfriend);
        }*/

       /* if (userFriendsList.contains(unfriendId)) {
            userFriendsList.remove(unfriendId);
            //unfriendFriendsList.remove(userId);
            user.setFriendList(userFriendsList);
            //unfriend.setFriendList(unfriendFriendsList);
            userStorage.updateUser(user);
            //userStorage.updateUser(unfriend);
        }*/

    }

    public List<User> getAllFriends(long userId) {
        User user = userStorage.getUser(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден.");
        }
        List<User> result = new ArrayList<>();
        List<Long> friendList = user.getFriendList();
        for (Long id : friendList) {
            result.add(userStorage.getUser(id));
        }
        return result;
    }

    public List<User> getCommonFriends(long userId, long friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        if ((user == null) || (friend == null)) {
            throw new NotFoundException("Пользователь не найден");
        }
        List<User> result = new ArrayList<>();
        List<Long> userFriends = user.getFriendList();
        List<Long> friendFriends = friend.getFriendList();
        if ((!userFriends.isEmpty()) && (!friendFriends.isEmpty())) {
            for (Long userFriendId : userFriends) {
                if (friendFriends.contains(userFriendId)) {
                    result.add(userStorage.getUser(userFriendId));
                }
            }

        }
        return result;
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
