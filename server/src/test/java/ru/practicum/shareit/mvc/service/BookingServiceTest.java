package ru.practicum.shareit.mvc.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class BookingServiceTest {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Test
    void shouldRejectBookingWithInvalidDates() {
        // Создаем владельца и вещь
        UserDto owner = createUser("Owner", "owner@test.com");
        ItemResponseDto item = createItem(owner.getId(), "Item", "Description");

        // Создаем бронирующего
        UserDto booker = createUser("Booker", "booker@test.com");

        // Пытаемся создать бронирование с end раньше start
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(2));
        dto.setEnd(LocalDateTime.now().plusDays(1)); // End раньше Start!

        assertThrows(BadRequestException.class, () ->
                bookingService.create(booker.getId(), dto)
        );
    }

    @Test
    void shouldRejectBookingByOwner() {
        // Создаем владельца и вещь
        UserDto owner = createUser("Owner", "owner@test.com");
        ItemResponseDto item = createItem(owner.getId(), "Item", "Description");

        // Владелец пытается забронировать свою вещь
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        assertThrows(BadRequestException.class, () ->
                bookingService.create(owner.getId(), dto)
        );
    }

    @Test
    void shouldFilterBookingsByState() {
        // Создаем владельца и вещь
        UserDto owner = createUser("Owner", "owner@test.com");
        ItemResponseDto item = createItem(owner.getId(), "Item", "Description");

        // Создаем бронирующего
        UserDto booker = createUser("Booker", "booker@test.com");

        // Создаем будущее бронирование
        BookingCreateDto futureDto = new BookingCreateDto();
        futureDto.setItemId(item.getId());
        futureDto.setStart(LocalDateTime.now().plusDays(1));
        futureDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.create(booker.getId(), futureDto);

        // Получаем все бронирования
        List<BookingResponseDto> all = bookingService.findAllByUser(booker.getId(), "ALL");
        assertEquals(1, all.size());

        // Получаем будущие бронирования
        List<BookingResponseDto> future = bookingService.findAllByUser(booker.getId(), "FUTURE");
        assertEquals(1, future.size());

        // Получаем ожидающие подтверждения
        List<BookingResponseDto> waiting = bookingService.findAllByUser(booker.getId(), "WAITING");
        assertEquals(1, waiting.size());
        assertEquals(BookingStatus.WAITING, waiting.getFirst().getStatus());
    }

    @Test
    void shouldRejectDoubleApproval() {
        // Создаем владельца и вещь
        UserDto owner = createUser("Owner", "owner@test.com");
        ItemResponseDto item = createItem(owner.getId(), "Item", "Description");

        // Создаем бронирующего и бронирование
        UserDto booker = createUser("Booker", "booker@test.com");
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        BookingResponseDto booking = bookingService.create(booker.getId(), dto);

        // Подтверждаем первый раз
        bookingService.approve(owner.getId(), booking.getId(), true);

        // Пытаемся подтвердить второй раз
        assertThrows(BadRequestException.class, () ->
                bookingService.approve(owner.getId(), booking.getId(), true)
        );
    }

    @Test
    void shouldThrowNotFoundForNonExistentBooking() {
        UserDto user = createUser("User", "user@test.com");

        assertThrows(NotFoundException.class, () ->
                bookingService.findById(user.getId(), 999L)
        );
    }

    private UserDto createUser(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return userService.create(dto);
    }

    private ItemResponseDto createItem(Long ownerId, String name, String description) {
        ItemCreateDto dto = new ItemCreateDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(true);
        return itemService.create(ownerId, dto);
    }
}
