package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@Component
@Primary
public class FilmDbStorage implements FilmStorage {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film createFilm(Film film) {
        validateFilm(film);
        if (film.getId() == null) {
            film.setId(generateId());
        }
        String sql = "INSERT INTO FILMS (FILMID, NAME, DESCRIPTION, RELEASEDATE, DURATION, RATINGMPAID)\n" +
                "VALUES(?, ?, ?, ?, ?, ?);";
        jdbcTemplate.update(sql, film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId());
        Iterator<Genre> it = film.getGenres().iterator();
        while (it.hasNext()) {
            sql = "INSERT INTO FILMGENRE (FILMID, GENREID) VALUES(?, ?);";
            jdbcTemplate.update(sql, film.getId(), it.next().getId());
        }
        log.info("Создан фильм {}", film);
        return getFilmById(film.getId());
    }

    @Override
    public Collection<Film> getFilms() {
        String sql = "SELECT FILMID, NAME, DESCRIPTION, RELEASEDATE, DURATION, RATINGMPAID FROM FILMS;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film updateFilm(Film film) {
        validateFilm(film);
        String sql = "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASEDATE = ?, DURATION = ?, RATINGMPAID = ? WHERE FILMID = ?;";
        getFilmById(film.getId());
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());

        Iterator<Genre> it = film.getGenres().iterator();
        sql = "DELETE FROM FILMGENRE WHERE FILMID = ?;";
        jdbcTemplate.update(sql, film.getId());
        while (it.hasNext()) {
            sql = "INSERT INTO FILMGENRE (FILMID, GENREID) VALUES(?, ?);";
            jdbcTemplate.update(sql, film.getId(), it.next().getId());
        }
        log.info("Обновлен фильм {}", film);
        return getFilmById(film.getId());
    }

    @Override
    public Film getFilmById(Integer id) {
        String sql = "SELECT FILMID, NAME, DESCRIPTION, RELEASEDATE, DURATION, RATINGMPAID FROM FILMS WHERE FILMID = ?;";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), id);
        if (films.isEmpty()) {
            log.info("Фильма с id = " + id + " не существует");
            throw new ObjectNotFoundException("Фильма с id = " + id + " не существует");
        }
        return films.get(0);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Integer filmId = rs.getInt("FILMID");
        String name = rs.getString("NAME");
        String description = rs.getString("DESCRIPTION");
        LocalDate releaseDate = rs.getDate("RELEASEDATE").toLocalDate();
        Integer duration = rs.getInt("DURATION");
        // Заполняем MPA
        Integer ratingMPA = rs.getInt("RATINGMPAID");
        String sql = "SELECT RATINGMPAID, NAME FROM RATINGMPA WHERE RATINGMPAID = ?;";
        String mpaName = "";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, ratingMPA);
        if (sqlRowSet.next()) {
            mpaName = sqlRowSet.getString("NAME");
        }
        // Заполняем лайки
        Set<Integer> likes = new HashSet<>();
        sql = "SELECT USERID FROM LIKES WHERE FILMID = ?;";
        sqlRowSet = jdbcTemplate.queryForRowSet(sql, filmId);
        while (sqlRowSet.next()) {
            likes.add(sqlRowSet.getInt("USERID"));
        }
        // Заполняем жанры
        Set<Genre> genres = new HashSet<>();
        sql = "SELECT G.GENREID, G.NAME  FROM FILMGENRE FG LEFT JOIN GENRES G ON FG.GENREID = G.GENREID \n" +
                "WHERE FG.FILMID = ?;";
        genres = Set.copyOf(jdbcTemplate.query(sql, (rs1, rowNum) -> makeGenre(rs1), filmId));
        return new Film(filmId, name, description, releaseDate, duration, new RatingMPA(ratingMPA, mpaName), likes, genres);
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return new Genre(rs.getInt("GENREID"), rs.getString("NAME"));
    }

    private Integer generateId() {

        String sql = "SELECT MAX(FILMID) AS \"lastId\" FROM FILMS;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        Integer count = 0;
        if (rowSet.next()) {
            count = rowSet.getInt("lastId");
        }
        return ++count;
    }

    private void validateFilm(Film film) throws ValidationException {
        if (film.getName().isBlank()) {
            log.warn("Название не может быть пустым\n" + film);
            throw new ValidationException("Название не может быть пустым");
        } else if (film.getDescription().length() > 200) {
            log.warn("Описание слишком длинное\n" + film);
            throw new ValidationException("Описание слишком длинное");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.warn("Фильм слишком старый\n" + film);
            throw new ValidationException("Фильм слишком старый");
        } else if (film.getDuration() <= 0) {
            log.warn("Продолжительность фильма должна быть положительной\n" + film);
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}
