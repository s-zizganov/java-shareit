package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import jakarta.validation.Valid;

/**
 * Контроллер для управления бронированиями в gateway.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                                @Valid @RequestBody BookingRequestDto bookingDto) {
        return bookingClient.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                                 @PathVariable Long bookingId,
                                                 @RequestParam boolean approved) {
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @PathVariable Long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader(USER_ID_HEADER) Long userId,
                                                   @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getOwnerBookings(userId, state);
    }
}