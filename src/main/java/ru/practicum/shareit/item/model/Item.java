package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private Long id;
    // за пользователем будет закрепляться вещь, он будет владельцем
    private Long userId;
    private String name;
    private String description;
    // разрешил ли аренду вещи владелец
    private Boolean available;
    // если нужная вешь не найдена при поиске
    private ItemRequest itemRequest;
}
