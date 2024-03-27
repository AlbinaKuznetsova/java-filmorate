package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.dao.UserServiceDao;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {
    private final JdbcTemplate jdbcTemplate;
    UserController controller;
    UserServiceDao userServiceDao;

    @BeforeEach
    void beforeEach() {
        UserStorage userStorage = new UserDbStorage(jdbcTemplate);
        userServiceDao = new UserServiceDao(jdbcTemplate);
        UserService userService = new UserService(userStorage, userServiceDao);
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

    @Test
    void createAndDeleteFriend() {
        User user = new User();
        user.setLogin("testlogin");
        user.setEmail("test@yandex.ru");
        user.setBirthday(LocalDate.of(2000, 12, 1));
        User user2 = new User();
        user2.setLogin("test2login");
        user2.setEmail("test2@yandex.ru");
        user2.setBirthday(LocalDate.of(1990, 12, 1));
        controller.create(user);
        controller.create(user2);
        assertEquals(2, controller.getUsers().size());
        controller.createFriend(user.getId(), user2.getId());
        assertTrue(controller.getFriends(user.getId()).contains(user2));
        assertFalse(controller.getFriends(user2.getId()).contains(user));

        User user3 = new User();
        user3.setLogin("test3login");
        user3.setEmail("test3@yandex.ru");
        user3.setBirthday(LocalDate.of(1990, 12, 1));
        controller.create(user3);
        assertEquals(0, controller.getFriends(user3.getId()).size());
        assertEquals(0, controller.getCommonFriends(user2.getId(), user3.getId()).size());

        controller.createFriend(user3.getId(), user2.getId());
        assertTrue(controller.getCommonFriends(user.getId(), user3.getId()).contains(user2));

        controller.deleteFriend(user.getId(), user2.getId());
        assertFalse(controller.getFriends(user.getId()).contains(user2));

    }
}
