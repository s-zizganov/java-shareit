package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Аннотация @RestController указывает, что это REST-контроллер
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    // Константа для имени заголовка
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    // Метод для создания нового бронирования
    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                                            @RequestBody BookingRequestDto bookingDto) {
        return ResponseEntity.status(201).body(bookingService.createBooking(userId, bookingDto));
    }

    // Метод для подтверждения или отклонения бронирования
    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> approveBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                                             @PathVariable Long bookingId,
                                                             @RequestParam boolean approved) {
        return ResponseEntity.ok(bookingService.approveBooking(userId, bookingId, approved));
    }

    // Метод для получения информации о бронировании по ID
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                                         @PathVariable Long bookingId) {
        return bookingService.getBooking(userId, bookingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Метод для получения списка бронирований пользователя или владельца
    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getBookings(@RequestHeader(USER_ID_HEADER) Long userId,
                                                                @RequestParam(defaultValue = "ALL") String state) {
        return ResponseEntity.ok(bookingService.getBookings(userId, state));
    }

    //  Метод для получения списка бронирований владельца
    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseDto>> getOwnerBookings(@RequestHeader(USER_ID_HEADER) Long userId,
                                                                     @RequestParam(defaultValue = "ALL") String state) {
        return ResponseEntity.ok(bookingService.getOwnerBookings(userId, state));
    }

}