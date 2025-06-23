package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.HashMap;
import java.util.Map;

// Аннотация @RestController указывает, что это REST-контроллер
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    // Хранилище бронирований в памяти, ключ — ID бронирования
    private final Map<Long, Booking> bookings = new HashMap<>();
    // Счётчик для генерации уникальных ID
    private Long idCounter = 1L;
    // Константа для имени заголовка
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    // Метод для создания нового бронирования
    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                                    @RequestBody BookingDto bookingDto) {
        // Проверяем, что пользователь существует (заглушка)
        if (!isValidUser(userId)) {
            return ResponseEntity.badRequest().build(); // 400, если пользователь недействителен
        }
        // Проверяем, что даты корректны (start < end)
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null ||
                bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            return ResponseEntity.badRequest().build(); // 400, если даты некорректны
        }
        // Создаём новую сущность бронирования
        Booking booking = new Booking();
        booking.setId(idCounter++);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItemId(bookingDto.getItemId());
        booking.setBookerId(userId);
        booking.setStatus("WAITING"); // Начальный статус — ожидание подтверждения
        // Сохраняем бронирование
        bookings.put(booking.getId(), booking);
        // Возвращаем DTO с кодом 201, используя маппер
        return ResponseEntity.status(201).body(BookingMapper.toBookingDto(booking));
    }

    // Метод для подтверждения или отклонения бронирования
    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                                     @PathVariable Long bookingId,
                                                     @RequestParam boolean approved) {
        // Получаем бронирование по ID
        Booking booking = bookings.get(bookingId);
        // Проверяем, существует ли бронирование и принадлежит ли вещь пользователю
        if (booking == null || !isItemOwner(booking.getItemId(), userId)) {
            return ResponseEntity.notFound().build(); // 404, если не найдено или не владелец
        }
        // Проверяем, что статус ещё "WAITING"
        if (!"WAITING".equals(booking.getStatus())) {
            return ResponseEntity.badRequest().build(); // 400, если статус уже изменён
        }
        // Устанавливаем новый статус
        booking.setStatus(approved ? "APPROVED" : "REJECTED");
        bookings.put(bookingId, booking);
        // Возвращаем обновлённое бронирование, используя маппер
        return ResponseEntity.ok(BookingMapper.toBookingDto(booking));
    }

    // Метод для получения информации о бронировании по ID
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                                 @PathVariable Long bookingId) {
        // Получаем бронирование по ID
        Booking booking = bookings.get(bookingId);
        // Проверяем, существует ли бронирование и имеет ли пользователь доступ (владелец или арендатор)
        if (booking == null || (!booking.getBookerId().equals(userId) && !isItemOwner(booking.getItemId(), userId))) {
            return ResponseEntity.notFound().build(); // 404, если не найдено или нет доступа
        }
        // Возвращаем бронирование в формате DTO, используя маппер
        return ResponseEntity.ok(BookingMapper.toBookingDto(booking));
    }

    // Метод для получения списка бронирований пользователя или владельца
    @GetMapping
    public ResponseEntity<Map<Long, BookingDto>> getBookings(@RequestHeader(USER_ID_HEADER) Long userId,
                                                             @RequestParam(required = false) String state) {
        // Создаём мапу для хранения DTO бронирований
        Map<Long, BookingDto> userBookings = new HashMap<>();
        // Фильтр по статусу (по умолчанию — все)
        String filterState = state != null ? state.toUpperCase() : "ALL";
        for (Map.Entry<Long, Booking> entry : bookings.entrySet()) {
            Booking booking = entry.getValue();
            boolean isOwner = isItemOwner(booking.getItemId(), userId);
            boolean isBooker = booking.getBookerId().equals(userId);
            if (isBooker || isOwner) {
                // Простая фильтрация по статусу
                if ("ALL".equals(filterState) || booking.getStatus().equals(filterState)) {
                    userBookings.put(booking.getId(), BookingMapper.toBookingDto(booking));
                }
            }
        }
        // Возвращаем список бронирований
        return ResponseEntity.ok(userBookings);
    }

    // Вспомогательный метод для проверки существования пользователя
    private boolean isValidUser(Long userId) {
        // Заглушка, в реальном случае нужно проверить наличие пользователя в UserController или сервисе
        return true;
    }

    // Вспомогательный метод для проверки, является ли пользователь владельцем вещи
    private boolean isItemOwner(Long itemId, Long userId) {
        // Заглушка, в реальном случае нужно проверить владельца вещи в ItemController или сервисе
        return true; // Предполагаем, что пользователь является владельцем для простоты
    }
}