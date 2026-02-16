package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Test
    void shouldCreateAndGetItemsByOwner() {
        // Создаем пользователя
        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");
        UserDto savedUser = userService.create(userDto);

        // Создаем вещь
        ItemCreateDto itemDto = new ItemCreateDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        ItemResponseDto createdItem = itemService.create(savedUser.getId(), itemDto);

        // Проверяем что вещь создалась
        assertThat(createdItem.getId()).isNotNull();
        assertThat(createdItem.getName()).isEqualTo("Test Item");
        assertThat(createdItem.getDescription()).isEqualTo("Test Description");

        // Получаем все вещи владельца
        List<ItemResponseDto> items = itemService.findAllByOwner(savedUser.getId());

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Test Item");
    }

    @Test
    void shouldSearchItemsByText() {
        // Создаем пользователя
        UserDto userDto = new UserDto();
        userDto.setName("Owner");
        userDto.setEmail("owner@example.com");
        UserDto savedUser = userService.create(userDto);

        // Создаем несколько вещей
        ItemCreateDto item1 = new ItemCreateDto();
        item1.setName("Дрель");
        item1.setDescription("Простая дрель");
        item1.setAvailable(true);
        itemService.create(savedUser.getId(), item1);

        ItemCreateDto item2 = new ItemCreateDto();
        item2.setName("Отвертка");
        item2.setDescription("Аккумуляторная отвертка");
        item2.setAvailable(true);
        itemService.create(savedUser.getId(), item2);

        // Ищем по тексту
        List<ItemResponseDto> results = itemService.search("дрель");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Дрель");
    }
}