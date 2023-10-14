package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getAllUsers();

    User createUser(User user);

    User updateUser(User user);

    User getUser(long userId);

    boolean addFriendListForID(long senderId, long recipientId);
    boolean removeFriendListForID(long senderId, long recipientId);
}
