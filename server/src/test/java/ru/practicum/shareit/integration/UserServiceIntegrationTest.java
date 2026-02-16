package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void shouldCreateAndFindUser() {
        UserDto dto = new UserDto();
        dto.setName("Test User");
        dto.setEmail("test@example.com");

        UserDto created = userService.create(dto);

        assertNotNull(created.getId());
        assertEquals("Test User", created.getName());
        assertEquals("test@example.com", created.getEmail());

        UserDto found = userService.findById(created.getId());
        assertEquals(created.getId(), found.getId());
    }

    @Test
    void shouldUpdateUser() {
        UserDto dto = new UserDto();
        dto.setName("Original");
        dto.setEmail("original@example.com");
        UserDto created = userService.create(dto);

        UserDto updateDto = new UserDto();
        updateDto.setName("Updated");
        updateDto.setEmail("updated@example.com");

        UserDto updated = userService.update(created.getId(), updateDto);

        assertEquals("Updated", updated.getName());
        assertEquals("updated@example.com", updated.getEmail());
    }

    @Test
    void shouldDeleteUser() {
        UserDto dto = new UserDto();
        dto.setName("To Delete");
        dto.setEmail("delete@example.com");
        UserDto created = userService.create(dto);

        userService.delete(created.getId());

        assertThrows(NotFoundException.class, () ->
                userService.findById(created.getId())
        );
    }

    @Test
    void shouldFindAllUsers() {
        UserDto user1 = new UserDto();
        user1.setName("User1");
        user1.setEmail("user1@example.com");
        userService.create(user1);

        UserDto user2 = new UserDto();
        user2.setName("User2");
        user2.setEmail("user2@example.com");
        userService.create(user2);

        List<UserDto> all = userService.findAll();
        assertTrue(all.size() >= 2);
    }

    @Test
    void shouldThrowNotFoundForNonExistentUser() {
        assertThrows(NotFoundException.class, () ->
                userService.findById(999L)
        );
    }

    @Test
    void shouldRejectDuplicateEmail() {
        UserDto user1 = new UserDto();
        user1.setName("User1");
        user1.setEmail("duplicate@example.com");
        userService.create(user1);

        UserDto user2 = new UserDto();
        user2.setName("User2");
        user2.setEmail("duplicate@example.com");

        assertThrows(ConflictException.class, () ->
                userService.create(user2)
        );
    }
}