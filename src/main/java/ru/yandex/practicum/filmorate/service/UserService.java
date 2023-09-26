package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class UserService {  //добавление в друзья, удаление из друзей, вывод списка общих друзей

    private final Map<Long, Set<Long>> friendsList = new HashMap(); //id-пользователя и id-друзей

    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(long userId, long friendId) {
        checkUser(userId);
        checkUser(friendId);
        System.out.println("Чек пройден: " + userId + " , " + friendId);
        Set<Long> userFriends;
        Set<Long> friendFriends;
        if (friendsList.containsKey(userId)) {
            userFriends = friendsList.get(userId);
            friendFriends = friendsList.get(friendId);
        } else {
            userFriends = new HashSet<>();
            friendFriends = new HashSet<>();
        }
        userFriends.add(friendId);
        friendsList.put(userId, userFriends);
        friendFriends.add(userId);
        friendsList.put(friendId, friendFriends);

    }

    public void deleteFriend(long userId, long unfriendId) {
        checkUser(userId);
        checkUser(unfriendId);
        if ((friendsList.containsKey(userId)) && (friendsList.containsKey(unfriendId))) {
            Set<Long> userFriends = friendsList.get(userId);
            Set<Long> unfriendFriends = friendsList.get(unfriendId);
            if (userFriends.contains(unfriendId)) {
                userFriends.remove(unfriendId);
                unfriendFriends.remove(userId);
            }
            friendsList.put(userId, userFriends);
            friendsList.put(unfriendId, unfriendFriends);
        }
    }

    public List<User> getAllFriends(long userId) {
        checkUser(userId);
        Set<Long> idFriends = friendsList.get(userId);
        List<User> result = new ArrayList<>();
        for (Long idFriend : idFriends) {
            result.add(userStorage.getUser(idFriend));
        }
        return result;
    }

    public List<User> getCommonFriends(long userId, long friendId) {
        checkUser(userId);
        checkUser(friendId);
        List<User> result = new ArrayList<>();
        Set<Long> userFriends = friendsList.get(userId);
        Set<Long> friendFriends = friendsList.get(friendId);
        if ((userFriends != null) && (friendFriends != null) && (!userFriends.isEmpty()) && (!friendFriends.isEmpty())) {
            for (Long userFriendId : userFriends) {
                if (friendFriends.contains(userFriendId)) {
                    result.add(userStorage.getUser(userFriendId));
                }
            }
            return result;
        }
        return new ArrayList<>();
    }

    public void addUserInList(long id) {
        if (!friendsList.containsKey(id)) {
            friendsList.put(id, new HashSet<>());
        }
    }

    private boolean checkUser(long id) {
        if (userStorage.getUser(id) != null) {
            return true;
        }
        return false;
    }
}
