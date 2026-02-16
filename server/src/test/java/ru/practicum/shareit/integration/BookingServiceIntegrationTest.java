package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Test
    void shouldCreateAndApproveBooking() {
        // Создаем владельца
        UserDto owner = new UserDto();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        UserDto savedOwner = userService.create(owner);

        // Создаем вещь
        ItemCreateDto itemDto = new ItemCreateDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        ItemResponseDto savedItem = itemService.create(savedOwner.getId(), itemDto);

        // Создаем бронирующего
        UserDto booker = new UserDto();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        UserDto savedBooker = userService.create(booker);

        // Создаем бронирование
        BookingCreateDto bookingDto = new BookingCreateDto();
        bookingDto.setItemId(savedItem.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingResponseDto createdBooking = bookingService.create(savedBooker.getId(), bookingDto);

        // Проверяем создание
        assertThat(createdBooking.getId()).isNotNull();
        assertThat(createdBooking.getStatus()).isEqualTo(BookingStatus.WAITING);

        // Подтверждаем бронирование
        BookingResponseDto approvedBooking = bookingService.approve(
                savedOwner.getId(),
                createdBooking.getId(),
                true
        );

        assertThat(approvedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void shouldGetAllUserBookings() {
        // Создаем владельца
        UserDto owner = new UserDto();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        UserDto savedOwner = userService.create(owner);

        // Создаем вещь
        ItemCreateDto itemDto = new ItemCreateDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        ItemResponseDto savedItem = itemService.create(savedOwner.getId(), itemDto);

        // Создаем бронирующего
        UserDto booker = new UserDto();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        UserDto savedBooker = userService.create(booker);

        // Создаем бронирование
        BookingCreateDto bookingDto = new BookingCreateDto();
        bookingDto.setItemId(savedItem.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.create(savedBooker.getId(), bookingDto);

        // Получаем все бронирования пользователя
        List<BookingResponseDto> bookings = bookingService.findAllByUser(savedBooker.getId(), "ALL");

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getBooker().getId()).isEqualTo(savedBooker.getId());
    }
}