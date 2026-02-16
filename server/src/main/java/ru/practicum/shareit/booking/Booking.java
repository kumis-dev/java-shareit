package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // какую вещь бронируют
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    // и кто бронирует
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id", nullable = false)
    private User booker;
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startTime;
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endTime;
    // статус для определения забронировал ли вещь владелец или нет и вернул ли потом
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus bookingStatus;
}
