package ru.practicum.shareit.mvc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utility.HttpHeaders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ErrorHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Test
    void shouldReturn404ForNonExistentUser() throws Exception {
        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400ForInvalidRequest() throws Exception {
        // Создаем пользователя
        UserDto user = new UserDto();
        user.setName("Test");
        user.setEmail("test@test.com");
        UserDto created = userService.create(user);

        mockMvc.perform(get("/bookings")
                        .header(HttpHeaders.USER_ID, created.getId())
                        .param("state", "INVALID_STATE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404ForNonExistentItem() throws Exception {
        // Создаем пользователя
        UserDto user = new UserDto();
        user.setName("Test2");
        user.setEmail("test2@test.com");
        UserDto created = userService.create(user);

        mockMvc.perform(get("/items/999")
                        .header(HttpHeaders.USER_ID, created.getId()))
                .andExpect(status().isNotFound());
    }
}