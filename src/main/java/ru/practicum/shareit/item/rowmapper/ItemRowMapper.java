package ru.practicum.shareit.item.rowmapper;

import org.springframework.jdbc.core.RowMapper;
import ru.practicum.shareit.item.model.Item;

import java.sql.ResultSet;
import java.sql.SQLException;

// заготовка для будущего тз
public class ItemRowMapper implements RowMapper<Item> {
    @Override
    public Item mapRow(ResultSet rs, int rowNum) throws SQLException {
        Item item = new Item();

        item.setId(rs.getLong("item_id"));
        item.setUserId(rs.getLong("user_id"));
        item.setName(rs.getString("name"));
        item.setAvailable(rs.getBoolean("available"));
        item.setDescription(rs.getString("description"));

        return item;
    }
}
