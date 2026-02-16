package ru.practicum.shareit.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utility.HttpHeaders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void shouldCreateItem() throws Exception {
        ItemCreateDto createDto = new ItemCreateDto();
        createDto.setName("Item");
        createDto.setDescription("Description");
        createDto.setAvailable(true);

        ItemResponseDto responseDto = new ItemResponseDto();
        responseDto.setId(1L);
        responseDto.setName("Item");
        responseDto.setDescription("Description");
        responseDto.setAvailable(true);

        when(itemService.create(eq(1L), any(ItemCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header(HttpHeaders.USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Item"));
    }

    @Test
    void shouldGetItemById() throws Exception {
        ItemResponseDto responseDto = new ItemResponseDto();
        responseDto.setId(1L);
        responseDto.setName("Item");
        responseDto.setDescription("Description");
        responseDto.setAvailable(true);

        when(itemService.findById(eq(1L), eq(1L))).thenReturn(responseDto);

        mockMvc.perform(get("/items/1")
                        .header(HttpHeaders.USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Item"));
    }

    @Test
    void shouldSearchItems() throws Exception {
        ItemResponseDto item = new ItemResponseDto();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Простая дрель");
        item.setAvailable(true);

        when(itemService.search("дрель")).thenReturn(List.of(item));

        mockMvc.perform(get("/items/search")
                        .param("text", "дрель"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Дрель"));
    }
}