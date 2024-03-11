package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.ValidationException;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmControllerTest {
    FilmController controller;
    UserStorage userStorage;

    @BeforeEach
    void beforeEach() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        FilmService filmService = new FilmService(filmStorage, userStorage);
        controller = new FilmController(filmService);
    }

    @Test
    void createFilmWrongName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("test");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void createFilmWrongDescription() {
        Film film = new Film();
        film.setName("test");
        film.setDescription("testttttttttttttttttttttt@Test\n" +
                "    void createFilmWrongName() {\n" +
                "        Film film = new Film();\n" +
                "        film.setName(\"\");\n" +
                "        film.setDescription(\"test\");\n" +
                "        film.setDuration(100);\n" +
                "        film.setReleaseDate(LocalDate.of(2000,1,1));\n" +
                "        assertThrows(ValidationException.class, ()-> controller.create(film));\n" +
                "    }");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void createFilmWrongDate() {
        Film film = new Film();
        film.setName("test");
        film.setDescription("test");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void createFilmWrongDuration() {
        Film film = new Film();
        film.setName("test");
        film.setDescription("test");
        film.setDuration(-100);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void createLike() {
        UserService userService = new UserService(userStorage);
        UserController userController = new UserController(userService);
        User user = new User();
        user.setLogin("testlogin");
        user.setEmail("test@yandex.ru");
        user.setBirthday(LocalDate.of(2000, 12, 1));
        userController.create(user);

        Film film = new Film();
        film.setName("test");
        film.setDescription("test");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        controller.create(film);

        Film film2 = new Film();
        film2.setName("test");
        film2.setDescription("test");
        film2.setDuration(100);
        film2.setReleaseDate(LocalDate.of(2000, 1, 1));
        controller.create(film2);

        controller.createLike(film.getId(), user.getId());
        assertTrue(controller.getFilm(film.getId()).getLikes().contains(user.getId()));

        assertEquals(0, controller.getPopularFilms(10).indexOf(film));

        controller.deleteLike(film.getId(), user.getId());
        assertFalse(controller.getFilm(film.getId()).getLikes().contains(user.getId()));

    }

    @Test
    void createLikeFromWrongUser() {

    }
}
