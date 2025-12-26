package ru.practicum.shareit.user.rowmapper;

import org.springframework.jdbc.core.RowMapper;
import ru.practicum.shareit.user.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

// заготовка для будущего тз
public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();

        user.setId(rs.getLong("user_id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));

        return user;
    }
}
