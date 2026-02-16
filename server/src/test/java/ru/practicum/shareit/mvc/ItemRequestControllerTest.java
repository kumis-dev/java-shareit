package ru.practicum.shareit.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.utility.HttpHeaders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void shouldCreateRequest() throws Exception {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Нужна дрель");

        ItemRequestDto responseDto = new ItemRequestDto();
        responseDto.setId(1L);
        responseDto.setDescription("Нужна дрель");
        responseDto.setCreateTime(LocalDateTime.now());
        responseDto.setItems(List.of());

        when(itemRequestService.create(eq(1L), any(ItemRequestCreateDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .header(HttpHeaders.USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Нужна дрель"));
    }

    @Test
    void shouldGetOwnRequests() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Нужна дрель");
        dto.setCreateTime(LocalDateTime.now());
        dto.setItems(List.of());

        when(itemRequestService.findAllByUser(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/requests")
                        .header(HttpHeaders.USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Нужна дрель"));
    }

    @Test
    void shouldGetAllRequests() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Нужен шуруповёрт");
        dto.setCreateTime(LocalDateTime.now());
        dto.setItems(List.of());

        when(itemRequestService.findAll(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/requests/all")
                        .header(HttpHeaders.USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void shouldGetRequestById() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Нужна дрель");
        dto.setCreateTime(LocalDateTime.now());
        dto.setItems(List.of());

        when(itemRequestService.findById(1L, 1L)).thenReturn(dto);

        mockMvc.perform(get("/requests/1")
                        .header(HttpHeaders.USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldDeleteRequest() throws Exception {
        doNothing().when(itemRequestService).delete(1L, 1L);

        mockMvc.perform(delete("/requests/1")
                        .header(HttpHeaders.USER_ID, 1L))
                .andExpect(status().isOk());

        verify(itemRequestService).delete(1L, 1L);
    }
}