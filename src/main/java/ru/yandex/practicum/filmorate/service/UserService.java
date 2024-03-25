package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.dao.UserServiceDao;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserServiceDao userServiceDao;

    public void createFriend(Integer userId, Integer friendId) {
        User user1 = userStorage.getUserById(userId);
        User user2 = userStorage.getUserById(friendId);
        userServiceDao.createFriend(userId, friendId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        try {
            User user1 = userStorage.getUserById(userId);
            User user2 = userStorage.getUserById(friendId);
            userServiceDao.deleteFriend(userId, friendId);
        } catch (ObjectNotFoundException e) {
            log.warn(e.getMessage());
        }
    }

    public List<User> getFriends(Integer id) {
        List<User> friendsList = new ArrayList<>();
        try {
            User user = userStorage.getUserById(id);
            Map<Integer, Boolean> friends = user.getFriends();
            for (Integer friend : friends.keySet()) {
                friendsList.add(userStorage.getUserById(friend));
            }
        } catch (ObjectNotFoundException e) {
            log.warn(e.getMessage());
        }
        return friendsList;
    }

    public List<User> getCommonFriends(Integer id, Integer id2) {
        User user = userStorage.getUserById(id);
        User user2 = userStorage.getUserById(id2);
        Map<Integer, Boolean> friends = user.getFriends();
        Map<Integer, Boolean> friends2 = user2.getFriends();
        List<Integer> list = friends.keySet().stream().filter(x -> friends2.keySet().contains(x)).collect(Collectors.toList());

        List<User> commonFriendsList = new ArrayList<>();
        for (Integer friend : list) {
            commonFriendsList.add(userStorage.getUserById(friend));
        }
        return commonFriendsList;
    }

    public UserStorage getUserStorage() {
        return userStorage;
    }
}
