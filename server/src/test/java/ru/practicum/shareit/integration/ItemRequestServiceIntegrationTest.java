package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private UserDto createUser(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return userService.create(dto);
    }

    @Test
    void shouldCreateRequest() {
        UserDto user = createUser("User", "user@example.com");

        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Нужна дрель");

        ItemRequestDto saved = itemRequestService.create(user.getId(), createDto);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDescription()).isEqualTo("Нужна дрель");
        assertThat(saved.getCreateTime()).isNotNull();
        assertThat(saved.getItems()).isEmpty();
    }

    @Test
    void shouldThrowNotFoundOnCreateWithBadUser() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Нужна дрель");

        assertThrows(NotFoundException.class, () -> itemRequestService.create(999L, createDto));
    }

    @Test
    void shouldFindAllByUser() {
        UserDto user = createUser("User", "user@example.com");

        ItemRequestCreateDto dto1 = new ItemRequestCreateDto();
        dto1.setDescription("Нужна дрель");
        itemRequestService.create(user.getId(), dto1);

        ItemRequestCreateDto dto2 = new ItemRequestCreateDto();
        dto2.setDescription("Нужен шуруповёрт");
        itemRequestService.create(user.getId(), dto2);

        List<ItemRequestDto> requests = itemRequestService.findAllByUser(user.getId());

        assertThat(requests).hasSize(2);
    }

    @Test
    void shouldFindAllExcludingOwnRequests() {
        UserDto user1 = createUser("User1", "user1@example.com");
        UserDto user2 = createUser("User2", "user2@example.com");

        ItemRequestCreateDto dto1 = new ItemRequestCreateDto();
        dto1.setDescription("Запрос от user1");
        itemRequestService.create(user1.getId(), dto1);

        ItemRequestCreateDto dto2 = new ItemRequestCreateDto();
        dto2.setDescription("Запрос от user2");
        itemRequestService.create(user2.getId(), dto2);

        List<ItemRequestDto> requestsForUser1 = itemRequestService.findAll(user1.getId());

        assertThat(requestsForUser1).hasSize(1);
        assertThat(requestsForUser1.getFirst().getDescription()).isEqualTo("Запрос от user2");
    }

    @Test
    void shouldFindByIdWithItems() {
        UserDto requester = createUser("Requester", "requester@example.com");
        UserDto owner = createUser("Owner", "owner@example.com");

        ItemRequestCreateDto requestDto = new ItemRequestCreateDto();
        requestDto.setDescription("Нужна дрель");
        ItemRequestDto savedRequest = itemRequestService.create(requester.getId(), requestDto);

        ItemCreateDto itemDto = new ItemCreateDto();
        itemDto.setName("Дрель Bosch");
        itemDto.setDescription("Мощная дрель");
        itemDto.setAvailable(true);
        itemDto.setRequestId(savedRequest.getId());
        itemService.create(owner.getId(), itemDto);

        ItemRequestDto found = itemRequestService.findById(requester.getId(), savedRequest.getId());

        assertThat(found.getId()).isEqualTo(savedRequest.getId());
        assertThat(found.getDescription()).isEqualTo("Нужна дрель");
        assertThat(found.getItems()).hasSize(1);
        assertThat(found.getItems().getFirst().getName()).isEqualTo("Дрель Bosch");
    }

    @Test
    void shouldThrowNotFoundOnFindByBadId() {
        UserDto user = createUser("User", "user@example.com");

        assertThrows(NotFoundException.class, () -> itemRequestService.findById(user.getId(), 999L));
    }

    @Test
    void shouldDeleteOwnRequest() {
        UserDto user = createUser("User", "user@example.com");

        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Нужна дрель");
        ItemRequestDto saved = itemRequestService.create(user.getId(), createDto);

        itemRequestService.delete(user.getId(), saved.getId());

        assertThrows(NotFoundException.class, () -> itemRequestService.findById(user.getId(), saved.getId()));
    }

    @Test
    void shouldThrowForbiddenOnDeleteOtherUserRequest() {
        UserDto user1 = createUser("User1", "user1@example.com");
        UserDto user2 = createUser("User2", "user2@example.com");

        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Запрос user1");
        ItemRequestDto saved = itemRequestService.create(user1.getId(), createDto);

        assertThrows(ForbiddenException.class, () -> itemRequestService.delete(user2.getId(), saved.getId()));
    }

    @Test
    void shouldReturnItemsInFindAllByUser() {
        UserDto requester = createUser("Requester", "requester@example.com");
        UserDto owner = createUser("Owner", "owner@example.com");

        ItemRequestCreateDto requestDto = new ItemRequestCreateDto();
        requestDto.setDescription("Нужна дрель");
        ItemRequestDto savedRequest = itemRequestService.create(requester.getId(), requestDto);

        ItemCreateDto itemDto = new ItemCreateDto();
        itemDto.setName("Дрель Makita");
        itemDto.setDescription("Хорошая дрель");
        itemDto.setAvailable(true);
        itemDto.setRequestId(savedRequest.getId());
        itemService.create(owner.getId(), itemDto);

        List<ItemRequestDto> requests = itemRequestService.findAllByUser(requester.getId());

        assertThat(requests).hasSize(1);
        assertThat(requests.getFirst().getItems()).hasSize(1);
        assertThat(requests.getFirst().getItems().getFirst().getName()).isEqualTo("Дрель Makita");
    }
}