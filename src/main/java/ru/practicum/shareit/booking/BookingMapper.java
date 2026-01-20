package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    // Entity - Response DTO
    public static BookingResponseDto mapToResponseDto(Booking booking) {
        BookingResponseDto dto = new BookingResponseDto();

        dto.setId(booking.getId());
        dto.setStart(booking.getStartTime());
        dto.setEnd(booking.getEndTime());
        dto.setStatus(booking.getBookingStatus());

        ItemShortDto itemDto = ItemMapper.mapToShortDto(booking.getItem());
        dto.setItem(itemDto);

        UserShortDto userDto = new UserShortDto();
        userDto.setId(booking.getBooker().getId());
        dto.setBooker(userDto);

        return dto;
    }

    // Entity - Short DTO (для ItemResponseDto)
    public static BookingShortDto mapToShortDto(Booking booking) {
        BookingShortDto dto = new BookingShortDto();
        dto.setId(booking.getId());
        dto.setBookerId(booking.getBooker().getId());
        return dto;
    }

    // Create DTO - Entity
    public static Booking mapFromCreateDto(
            BookingCreateDto dto,
            Item item,
            User booker
    ) {
        Booking booking = new Booking();

        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStartTime(dto.getStart());
        booking.setEndTime(dto.getEnd());
        booking.setBookingStatus(BookingStatus.WAITING);

        return booking;
    }
}
