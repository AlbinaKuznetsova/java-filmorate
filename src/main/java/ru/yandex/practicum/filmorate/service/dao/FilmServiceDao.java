package ru.yandex.practicum.filmorate.service.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmServiceDao {
    private final JdbcTemplate jdbcTemplate;

    public void createLike(Integer filmId, Integer userId) {
        String sql1 = "SELECT * FROM LIKES WHERE FILMID = ? AND USERID = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql1, filmId, userId);
        if (!rowSet.next()) {
            String sql = "INSERT INTO LIKES (FILMID, USERID) VALUES (?, ?);";
            jdbcTemplate.update(sql, filmId, userId);
        }
    }

    public void deleteLike(Integer filmId, Integer userId) {
        String sql = "DELETE FROM LIKES WHERE FILMID = ? AND USERID = ?;";
        jdbcTemplate.update(sql, filmId, userId);
    }
    public Collection<Genre> getGenres() {
        String sql = "SELECT GENREID, NAME FROM GENRES;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    public Genre getGenreById(Integer id) {
        String sql = "SELECT GENREID, NAME FROM GENRES WHERE GENREID = ?;";
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs),id);
        if (genres.isEmpty()) {
            throw new ObjectNotFoundException("Жанра с id = " + id + " не существует");
        }
        return genres.get(0);
    }

    public Collection<RatingMPA> getMPA() {
        String sql = "SELECT RATINGMPAID, NAME FROM RATINGMPA;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMPA(rs));
    }

    public RatingMPA getMPAById(Integer id) {
        String sql = "SELECT RATINGMPAID, NAME FROM RATINGMPA WHERE RATINGMPAID = ?;";
        List<RatingMPA> mpa = jdbcTemplate.query(sql, (rs, rowNum) -> makeMPA(rs),id);
        if (mpa.isEmpty()) {
            throw new ObjectNotFoundException("Рейтинга с id = " + id + " не существует");
        }
        return mpa.get(0);
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return new Genre(rs.getInt("GENREID"), rs.getString("NAME"));
    }
    private RatingMPA makeMPA(ResultSet rs) throws SQLException {
        return new RatingMPA(rs.getInt("RATINGMPAID"), rs.getString("NAME"));
    }

}
