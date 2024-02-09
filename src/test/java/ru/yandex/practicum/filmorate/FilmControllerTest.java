package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.ValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class FilmControllerTest {
    FilmController controller;
    @BeforeEach
    void beforeEach() {
        controller = new FilmController();
    }
    @Test
    void createFilmWrongName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("test");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2000,1,1));
        assertThrows(ValidationException.class, ()-> controller.create(film));
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
        film.setReleaseDate(LocalDate.of(2000,1,1));
        assertThrows(ValidationException.class, ()-> controller.create(film));
    }
    @Test
    void createFilmWrongDate() {
        Film film = new Film();
        film.setName("test");
        film.setDescription("test");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(1800,1,1));
        assertThrows(ValidationException.class, ()-> controller.create(film));
    }
    @Test
    void createFilmWrongDuration() {
        Film film = new Film();
        film.setName("test");
        film.setDescription("test");
        film.setDuration(-100);
        film.setReleaseDate(LocalDate.of(2000,1,1));
        assertThrows(ValidationException.class, ()-> controller.create(film));
    }
}
