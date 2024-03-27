package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.dao.FilmServiceDao;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest // указываем, о необходимости подготовить бины для работы с БД
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    FilmDbStorage filmDbStorage;

    @BeforeEach
    void beforeEach() {
        filmDbStorage = new FilmDbStorage(jdbcTemplate);
    }

    @Test
    public void testFindFilmById() {

        Film film = new Film();
        film.setName("test");
        film.setDescription("test description");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(1995, 10, 1));
        film.setMpa(new RatingMPA(1, null));
        film.setGenres(new HashSet<>(List.of(new Genre(3, null), new Genre(5, null))));
        filmDbStorage.createFilm(film);

        Film savedFilm = filmDbStorage.getFilmById(film.getId());

        assertThat(savedFilm).isNotNull().usingRecursiveComparison().ignoringFields("mpa", "genres").isEqualTo(film);
    }

    @Test
    public void testUpdateFilm() {

        Film film = new Film();
        film.setName("test");
        film.setDescription("test description");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(1995, 10, 1));
        film.setMpa(new RatingMPA(1, null));
        film.setGenres(new HashSet<>(List.of(new Genre(3, null), new Genre(5, null))));
        filmDbStorage.createFilm(film);

        Film savedFilm = filmDbStorage.getFilmById(film.getId());

        film.setName("New Name");
        Film updatedFilm = filmDbStorage.updateFilm(film);
        assertThat(savedFilm).isNotNull().usingRecursiveComparison().isNotEqualTo(updatedFilm);
    }

    @Test
    public void testGetAllFilms() {

        Film film1 = new Film();
        film1.setName("test");
        film1.setDescription("test description");
        film1.setDuration(100);
        film1.setReleaseDate(LocalDate.of(1995, 10, 1));
        film1.setMpa(new RatingMPA(1, "G"));
        film1.setGenres(new HashSet<>(List.of(new Genre(3, "Мультфильм"))));

        Film film2 = new Film();
        film2.setName("test2");
        film2.setDescription("test2 description");
        film2.setDuration(30);
        film2.setReleaseDate(LocalDate.of(2012, 10, 1));
        film2.setMpa(new RatingMPA(2, "PG"));
        film2.setGenres(new HashSet<>(List.of(new Genre(2, "Драма"))));
        filmDbStorage.createFilm(film1);
        filmDbStorage.createFilm(film2);

        assertTrue(filmDbStorage.getFilms().contains(film1));
        assertTrue(filmDbStorage.getFilms().contains(film2));
    }

    @Test
    void createAndDeleteLike() {

        Film film1 = new Film();
        film1.setName("test");
        film1.setDescription("test description");
        film1.setDuration(100);
        film1.setReleaseDate(LocalDate.of(1995, 10, 1));
        film1.setMpa(new RatingMPA(1, "G"));
        film1.setGenres(new HashSet<>(List.of(new Genre(3, "Мультфильм"))));

        Film film2 = new Film();
        film2.setName("test2");
        film2.setDescription("test2 description");
        film2.setDuration(30);
        film2.setReleaseDate(LocalDate.of(2012, 10, 1));
        film2.setMpa(new RatingMPA(2, "PG"));
        film2.setGenres(new HashSet<>(List.of(new Genre(2, "Драма"))));
        filmDbStorage.createFilm(film1);
        filmDbStorage.createFilm(film2);

        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        User user = new User();
        user.setLogin("testlogin");
        user.setEmail("test@yandex.ru");
        user.setBirthday(LocalDate.of(2000, 12, 1));
        userStorage.createUser(user);

        FilmService filmService = new FilmService(filmDbStorage, userStorage, new FilmServiceDao(jdbcTemplate));
        assertEquals(2, filmDbStorage.getFilms().size());
        filmService.createLike(film1.getId(), user.getId());
        film1 = filmDbStorage.getFilmById(film1.getId());
        assertTrue(filmService.getPopular(1).contains(film1));
        assertFalse(filmService.getPopular(1).contains(film2));

    }

    @Test
    public void testGetRatingMPA() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        FilmService filmService = new FilmService(filmDbStorage, userStorage, new FilmServiceDao(jdbcTemplate));
        RatingMPA mpa = filmService.getMPAById(1);
        assertEquals(mpa, new RatingMPA(1, "G"));
        assert (filmService.getMPA().contains(mpa));
    }

    @Test
    public void testGetGenre() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        FilmService filmService = new FilmService(filmDbStorage, userStorage, new FilmServiceDao(jdbcTemplate));
        Genre genre = filmService.getGenreById(2);
        assertEquals(genre, new Genre(2, "Драма"));
        assertTrue(filmService.getGenres().contains(genre));
    }
}
