package ru.practicum.shareit.booking;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    private Long id;
    // какую вещь бронируют
    private Long itemId;
    // и кто бронирует
    private Long bookerId;
    private String review;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    // статус для определения забронировал ли вещь владелец или нет и вернул ли потом
    private BookingStatus bookingStatus;
}
