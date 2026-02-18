package ru.practicum.shareit.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void shouldCreateUser() throws Exception {
        UserDto dto = new UserDto();
        dto.setName("User");
        dto.setEmail("user@example.com");

        UserDto responseDto = new UserDto();
        responseDto.setId(1L);
        responseDto.setName("User");
        responseDto.setEmail("user@example.com");

        when(userService.create(any(UserDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("User"))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        UserDto dto = new UserDto();
        dto.setName("Updated");

        UserDto responseDto = new UserDto();
        responseDto.setId(1L);
        responseDto.setName("Updated");
        responseDto.setEmail("user@example.com");

        when(userService.update(eq(1L), any(UserDto.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService).delete(1L);
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setName("User1");
        user1.setEmail("user1@example.com");

        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setName("User2");
        user2.setEmail("user2@example.com");

        when(userService.findAll()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void shouldGetUserById() throws Exception {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("User");
        dto.setEmail("user@example.com");

        when(userService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("User"));
    }
}