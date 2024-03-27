package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@Component

public class InMemoryUserStorage implements UserStorage {
    private HashMap<Integer, User> users = new HashMap<Integer, User>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private static Integer count = 0;

    @Override
    public User createUser(User user) {
        validateUser(user);
        if (user.getId() == null) {
            user.setId(generateId());
        }
        users.put(user.getId(), user);
        log.info("Добавлен пользователь {}", user);
        return user;
    }

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User updateUser(User user) {
        validateUser(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Обновлен пользователь {}", user);
        } else {
            log.warn("Пользователя с id = {} не существует", user.getId());
            throw new ObjectNotFoundException("Пользователя с id = " + user.getId() + " не существует");
        }

        return user;
    }

    @Override
    public User getUserById(Integer id) {
        if (users.get(id) == null) {
            throw new ObjectNotFoundException("Пользователя с id = " + id + " не существует");
        }
        return users.get(id);
    }

    private Integer generateId() {
        return ++count;
    }

    private void validateUser(User user) throws ValidationException {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Неправильный email\n" + user);
            throw new ValidationException("Неправильный email");
        } else if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Неправильный логин\n" + user);
            throw new ValidationException("Неправильный логин");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Неправильная дата рождения\n" + user);
            throw new ValidationException("Неправильная дата рождения");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        } else if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
