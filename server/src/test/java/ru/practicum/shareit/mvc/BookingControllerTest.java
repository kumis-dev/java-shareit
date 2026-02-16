package ru.practicum.shareit.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.utility.HttpHeaders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void shouldCreateBooking() throws Exception {
        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(1L);
        createDto.setStart(LocalDateTime.now().plusDays(1));
        createDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(1L);
        responseDto.setStart(createDto.getStart());
        responseDto.setEnd(createDto.getEnd());
        responseDto.setStatus(BookingStatus.WAITING);

        ItemShortDto item = new ItemShortDto();
        item.setId(1L);
        item.setName("Item");
        responseDto.setItem(item);

        UserShortDto booker = new UserShortDto();
        booker.setId(1L);
        responseDto.setBooker(booker);

        when(bookingService.create(eq(1L), any(BookingCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header(HttpHeaders.USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void shouldApproveBooking() throws Exception {
        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(1L);
        responseDto.setStatus(BookingStatus.APPROVED);

        when(bookingService.approve(eq(1L), eq(1L), eq(true))).thenReturn(responseDto);

        mockMvc.perform(patch("/bookings/1")
                        .header(HttpHeaders.USER_ID, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void shouldGetAllUserBookings() throws Exception {
        BookingResponseDto booking = new BookingResponseDto();
        booking.setId(1L);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingService.findAllByUser(eq(1L), eq("ALL"))).thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings")
                        .header(HttpHeaders.USER_ID, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}