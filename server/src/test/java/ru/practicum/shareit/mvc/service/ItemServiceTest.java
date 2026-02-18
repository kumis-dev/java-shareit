package ru.practicum.shareit.mvc.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @Test
    void shouldNotAllowNonOwnerToUpdate() {
        // Создаем владельца и вещь
        UserDto owner = createUser("Owner", "owner@test.com");
        ItemResponseDto item = createItem(owner.getId(), "Item", "Description");

        // Создаем другого пользователя
        UserDto otherUser = createUser("Other", "other@test.com");

        // Пытаемся обновить чужую вещь
        ItemCreateDto updateDto = new ItemCreateDto();
        updateDto.setName("Updated");

        assertThrows(ForbiddenException.class, () ->
                itemService.update(otherUser.getId(), item.getId(), updateDto)
        );
    }

    @Test
    void shouldNotAllowNonOwnerToDelete() {
        // Создаем владельца и вещь
        UserDto owner = createUser("Owner", "owner@test.com");
        ItemResponseDto item = createItem(owner.getId(), "Item", "Description");

        // Создаем другого пользователя
        UserDto otherUser = createUser("Other", "other@test.com");

        // Пытаемся удалить чужую вещь
        assertThrows(ForbiddenException.class, () ->
                itemService.delete(item.getId(), otherUser.getId())
        );
    }

    @Test
    void shouldReturnEmptyListForBlankSearch() {
        List<ItemResponseDto> results = itemService.search("");
        assertTrue(results.isEmpty());

        results = itemService.search("   ");
        assertTrue(results.isEmpty());
    }

    @Test
    void shouldNotAllowCommentWithoutBooking() {
        // Создаем владельца и вещь
        UserDto owner = createUser("Owner", "owner@test.com");
        ItemResponseDto item = createItem(owner.getId(), "Item", "Description");

        // Создаем пользователя без бронирования
        UserDto user = createUser("User", "user@test.com");

        // Пытаемся оставить комментарий без бронирования
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        assertThrows(BadRequestException.class, () ->
                itemService.addComment(user.getId(), item.getId(), commentDto)
        );
    }

    @Test
    void shouldUpdateOnlyProvidedFields() {
        // Создаем владельца и вещь
        UserDto owner = createUser("Owner", "owner@test.com");
        ItemCreateDto createDto = new ItemCreateDto();
        createDto.setName("Original Name");
        createDto.setDescription("Original Description");
        createDto.setAvailable(true);
        ItemResponseDto item = itemService.create(owner.getId(), createDto);

        // Обновляем только имя
        ItemCreateDto updateDto = new ItemCreateDto();
        updateDto.setName("Updated Name");
        ItemResponseDto updated = itemService.update(owner.getId(), item.getId(), updateDto);

        assertEquals("Updated Name", updated.getName());
        assertEquals("Original Description", updated.getDescription()); // Не изменилось
        assertTrue(updated.getAvailable()); // Не изменилось
    }

    @Test
    void shouldThrowNotFoundForNonExistentItem() {
        UserDto user = createUser("User", "user@test.com");

        assertThrows(NotFoundException.class, () ->
                itemService.findById(user.getId(), 999L)
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
