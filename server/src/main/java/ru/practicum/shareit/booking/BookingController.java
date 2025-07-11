package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

/**
 * Контроллер для управления бронированиями
 * Обрабатывает HTTP-запросы, связанные с бронированием вещей
 */
// Аннотация @RestController указывает, что это REST-контроллер
@RestController
// Аннотация @RequestMapping задает базовый путь для всех методов контроллера
@RequestMapping(path = "/bookings")
// Аннотация @RequiredArgsConstructor автоматически создает конструктор для final полей
@RequiredArgsConstructor
public class BookingController {
    /**
     * Сервис для работы с бронированиями
     */
    private final BookingService bookingService;

    // Константа для имени заголовка
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    /**
     * Метод для создания нового бронирования
     * @param userId     идентификатор пользователя, создающего бронирование
     * @param bookingDto данные для создания бронирования
     * @return информация о созданном бронировании и статус 201 (Created)
     */
    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                                            @RequestBody BookingRequestDto bookingDto) {
        return ResponseEntity.status(201).body(bookingService.createBooking(userId, bookingDto));
    }

    /**
     * Метод для подтверждения или отклонения бронирования
     * @param userId    идентификатор пользователя (владельца вещи)
     * @param bookingId идентификатор бронирования
     * @param approved  флаг подтверждения (true - подтвердить, false - отклонить)
     * @return обновленная информация о бронировании
     */
    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> approveBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                                             @PathVariable Long bookingId,
                                                             @RequestParam boolean approved) {
        return ResponseEntity.ok(bookingService.approveBooking(userId, bookingId, approved));
    }

    /**
     * Метод для получения информации о бронировании по ID
     * @param userId    идентификатор пользователя (владельца или автора бронирования)
     * @param bookingId идентификатор бронирования
     * @return информация о бронировании или статус 404, если бронирование не найдено
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                                         @PathVariable Long bookingId) {
        return bookingService.getBooking(userId, bookingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Метод для получения списка бронирований пользователя
     * @param userId идентификатор пользователя
     * @param state  состояние бронирований для фильтрации (ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED)
     * @return список бронирований пользователя
     */
    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getBookings(@RequestHeader(USER_ID_HEADER) Long userId,
                                                                @RequestParam(defaultValue = "ALL") String state) {
        return ResponseEntity.ok(bookingService.getBookings(userId, state));
    }

    /**
     * Метод для получения списка бронирований владельца
     * @param userId идентификатор пользователя-владельца вещей
     * @param state  состояние бронирований для фильтрации (ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED)
     * @return список бронирований вещей, принадлежащих пользователю
     */
    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseDto>> getOwnerBookings(@RequestHeader(USER_ID_HEADER) Long userId,
                                                                     @RequestParam(defaultValue = "ALL") String state) {
        return ResponseEntity.ok(bookingService.getOwnerBookings(userId, state));
    }

}