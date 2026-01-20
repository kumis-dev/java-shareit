package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    // Item - ItemResponseDto (когда отдаём пользователю)
    public static ItemResponseDto mapToItemDto(Item item,
                                               BookingShortDto lastBooking,
                                               BookingShortDto nextBooking,
                                               List<CommentDto> comments) {
        ItemResponseDto dto = new ItemResponseDto();
        // возвращаем id чтобы пользователь мог отредактировать вещь или получить ее
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setLastBooking(lastBooking);
        dto.setNextBooking(nextBooking);
        dto.setComments(comments);

        return dto;
    }

    // Entity - Short DTO (для Booking)
    public static ItemShortDto mapToShortDto(Item item) {
        ItemShortDto dto = new ItemShortDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        return dto;
    }

    // Create DTO - Entity
    public static Item mapFromCreateDto(ItemCreateDto dto) {
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
