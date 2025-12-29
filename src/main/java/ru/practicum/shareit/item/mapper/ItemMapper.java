package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    // Item - ItemDto (когда отдаём пользователю)
    public static ItemDto mapToItemDto(Item item) {
        ItemDto dto = new ItemDto();

        // возвращаем id чтобы пользователь мог отредактировать вещь или получить ее
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());

        return dto;
    }

    // ItemDto - Item (когда получаем от пользователя)
    public static Item mapToItem(ItemDto dto) {
        Item item = new Item();

        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());

        return item;
    }

    public static void updateFields(Item item, Item newItem) {
        if (newItem.getName() != null)
            item.setName(newItem.getName());

        if (newItem.getDescription() != null)
            item.setDescription(newItem.getDescription());

        if (newItem.getAvailable() != null)
            item.setAvailable(newItem.getAvailable());
    }
}
