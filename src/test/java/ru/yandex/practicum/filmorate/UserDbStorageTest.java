package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.dao.UserServiceDao;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    UserDbStorage userStorage;

    @BeforeEach
    void beforeEach() {
        userStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    public void testFindUserById() {

        User newUser = new User();
        newUser.setLogin("testlogin");
        newUser.setEmail("test@yandex.ru");
        newUser.setBirthday(LocalDate.of(2000, 12, 1));
        userStorage.createUser(newUser);

        User savedUser = userStorage.getUserById(newUser.getId());

        assertThat(savedUser).isNotNull().usingRecursiveComparison().isEqualTo(newUser);
    }

    @Test
    public void testUpdateUser() {

        User newUser = new User();
        newUser.setLogin("testlogin");
        newUser.setEmail("test@yandex.ru");
        newUser.setBirthday(LocalDate.of(2000, 12, 1));
        userStorage.createUser(newUser);

        User savedUser = userStorage.getUserById(newUser.getId());

        newUser.setName("New Name");
        User updatedUser = userStorage.updateUser(newUser);
        assertThat(savedUser).isNotNull().usingRecursiveComparison().isNotEqualTo(updatedUser);
    }

    @Test
    public void testGetAllUsers() {

        User user = new User();
        user.setLogin("testlogin");
        user.setEmail("test@yandex.ru");
        user.setBirthday(LocalDate.of(2000, 12, 1));
        User user2 = new User();
        user2.setLogin("test2login");
        user2.setEmail("test2@yandex.ru");
        user2.setBirthday(LocalDate.of(1990, 12, 1));
        userStorage.createUser(user);
        userStorage.createUser(user2);

        assertTrue(userStorage.getUsers().contains(user));
        assertTrue(userStorage.getUsers().contains(user2));
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
        UserService userService = new UserService(userStorage, new UserServiceDao(jdbcTemplate));
        userStorage.createUser(user);
        userStorage.createUser(user2);
        assertEquals(2, userStorage.getUsers().size());
        userService.createFriend(user.getId(), user2.getId());
        assertTrue(userService.getFriends(user.getId()).contains(user2));
        assertFalse(userService.getFriends(user2.getId()).contains(user));

        User user3 = new User();
        user3.setLogin("test3login");
        user3.setEmail("test3@yandex.ru");
        user3.setBirthday(LocalDate.of(1990, 12, 1));
        userStorage.createUser(user3);
        assertEquals(0, userService.getFriends(user3.getId()).size());
        assertEquals(0, userService.getCommonFriends(user2.getId(), user3.getId()).size());

        userService.createFriend(user3.getId(), user2.getId());
        assertTrue(userService.getCommonFriends(user.getId(), user3.getId()).contains(user2));

        userService.deleteFriend(user.getId(), user2.getId());
        assertFalse(userService.getFriends(user.getId()).contains(user2));

    }
}
