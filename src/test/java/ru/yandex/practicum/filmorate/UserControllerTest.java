package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.ValidationException;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

@SpringBootTest
public class UserControllerTest {
    UserController controller;

    @BeforeEach
    void beforeEach() {
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        controller = new UserController(userService);
    }

    @Test
    void createUserWrongLogin() {
        User user = new User();
        user.setLogin("test login");
        user.setEmail("test@yandex.ru");
        user.setBirthday(LocalDate.of(1991, 12, 1));
        user.setName("test");
        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void createUserWrongEmail() {
        User user = new User();
        user.setLogin("testlogin");
        user.setEmail("testyandex.ru");
        user.setBirthday(LocalDate.of(1991, 12, 1));
        user.setName("test");
        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void createUserWrongBirthday() {
        User user = new User();
        user.setLogin("testlogin");
        user.setEmail("test@yandex.ru");
        user.setBirthday(LocalDate.of(2026, 12, 1));
        user.setName("test");
        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void createUserWithoutName() {
        User user = new User();
        user.setLogin("testlogin");
        user.setEmail("test@yandex.ru");
        user.setBirthday(LocalDate.of(2000, 12, 1));
        try {
            controller.create(user);
            User user2 = controller.getUsers().stream().findFirst().get();
            assertEquals(user2.getLogin(), user2.getName());
        } catch (ValidationException exp) {
            System.out.println(exp.getMessage());
        }
    }
}
