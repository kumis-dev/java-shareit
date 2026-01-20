package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.utility.HttpHeaders;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto create(@RequestHeader(HttpHeaders.USER_ID) Long userId,
                                     @Valid @RequestBody BookingCreateDto dto) {
        return bookingService.create(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(@RequestHeader(HttpHeaders.USER_ID) Long ownerId,
                                      @PathVariable Long bookingId,
                                      @RequestParam boolean approved) {
        return bookingService.approve(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto findById(@RequestHeader(HttpHeaders.USER_ID) Long userId,
                                       @PathVariable Long bookingId) {
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> findAllByUser(@RequestHeader(HttpHeaders.USER_ID) Long userId,
                                                  @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.findAllByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> findAllByOwner(@RequestHeader(HttpHeaders.USER_ID) Long ownerId,
                                                   @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.findAllByOwner(ownerId, state);
    }
}
