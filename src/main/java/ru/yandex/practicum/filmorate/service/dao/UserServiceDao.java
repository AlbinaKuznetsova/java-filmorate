package ru.yandex.practicum.filmorate.service.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceDao {
    private final JdbcTemplate jdbcTemplate;

    public void createFriend(Integer userId, Integer friendId) {
        String sql1 = "SELECT * FROM FRIENDS WHERE USERID=? AND FRIENDID=?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql1, userId, friendId);
        if (!rowSet.next()) {
            String sql = "INSERT INTO FRIENDS (USERID, FRIENDID, STATUS) VALUES (?, ?, FALSE);";
            jdbcTemplate.update(sql, userId, friendId);
        }
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        String sql = "DELETE FROM FRIENDS WHERE USERID=? AND FRIENDID=?;";
        jdbcTemplate.update(sql, userId, friendId);
    }
}
