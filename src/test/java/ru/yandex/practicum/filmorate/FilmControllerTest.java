package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.ValidationException;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class FilmControllerTest {
    FilmController controller;

    @BeforeEach
    void beforeEach() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
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
}
