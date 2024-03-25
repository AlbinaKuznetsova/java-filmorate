package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
@Primary
public class UserDbStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        validateUser(user);
        if (user.getId() == null) {
            user.setId(generateId());
        }
        String sql = "INSERT INTO USERS (USERID, EMAIL, LOGIN, NAME, BIRTHDAY) VALUES(?, ?, ?, ? ,?);";
        jdbcTemplate.update(sql, user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        log.info("Добавлен пользователь {}", user);
        return user;
    }

    @Override
    public Collection<User> getUsers() {
        String sql = "SELECT USERID, EMAIL, LOGIN, NAME, BIRTHDAY FROM USERS;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User updateUser(User user) {
        validateUser(user);
        getUserById(user.getId());
        String sql = "UPDATE USERS SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? WHERE USERID = ?;";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return getUserById(user.getId());
    }

    @Override
    public User getUserById(Integer id) {
        String sql = "SELECT USERID, EMAIL, LOGIN, NAME, BIRTHDAY FROM USERS WHERE USERID = ?;";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id);
        if (users.isEmpty()) {
            log.info("Пользователя с id = " + id + " не существует");
            throw new ObjectNotFoundException("Пользователя с id = " + id + " не существует");
        }
        return users.get(0);
    }

    private Integer generateId() {

        String sql = "SELECT MAX(USERID) AS \"lastId\" FROM USERS;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        Integer count = 0;
        if (rowSet.next()) {
            count = rowSet.getInt("lastId");
        }
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

    private User makeUser(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("USERID");
        String email = rs.getString("EMAIL");
        String login = rs.getString("LOGIN");
        String name = rs.getString("NAME");

        LocalDate birthday = rs.getDate("BIRTHDAY").toLocalDate();

        Map<Integer, Boolean> friends = new HashMap<>();
        // Заполняем друзей
        String sql = "SELECT FRIENDID, STATUS FROM FRIENDS WHERE USERID = ?;";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, id);
        while (sqlRowSet.next()) {
            friends.put(sqlRowSet.getInt("FRIENDID"), sqlRowSet.getBoolean("STATUS"));
        }
        return new User(id, email, login, name, birthday, friends);
    }
}
