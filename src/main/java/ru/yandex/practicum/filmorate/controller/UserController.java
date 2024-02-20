package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.ValidationException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@RestController
public class UserController {
    private HashMap<Integer, User> users = new HashMap<Integer, User>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private static Integer count = 0;

    @PostMapping("/users")
    public User create(@Valid @RequestBody User user) throws ValidationException {
        validateUser(user);
        if (user.getId() == null) {
            user.setId(generateId());
        }
        users.put(user.getId(), user);
        log.info("Добавлен пользователь {}", user);
        return user;
    }

    @GetMapping("/users")
    public Collection<User> getUsers() {
        return users.values();
    }

    @PutMapping("/users")
    public User update(@Valid @RequestBody User user) throws ValidationException {
        validateUser(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Обновлен пользователь {}", user);
        } else {
            log.warn("Пользователя с id = {} не существует", user.getId());
            throw new ValidationException("Пользователя с id = " + user.getId() + " не существует");
        }

        return user;
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
