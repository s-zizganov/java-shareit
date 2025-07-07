package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;
import java.util.Optional;


  // Сервис для работы с бронированиями.
public interface BookingService {
    // Создает новое бронирование.
    BookingResponseDto createBooking(Long userId, BookingRequestDto bookingDto);

    // Подтверждает или отклоняет бронирование.
    BookingResponseDto approveBooking(Long userId, Long bookingId, boolean approved);

    // Получает информацию о бронировании по его идентификатору.

    Optional<BookingResponseDto> getBooking(Long userId, Long bookingId);

    // Получает список бронирований пользователя.
    List<BookingResponseDto> getBookings(Long userId, String state);

    // Получает список бронирований для вещей, принадлежащих пользователю.
    List<BookingResponseDto> getOwnerBookings(Long userId, String state);

    // Получает список бронирований для конкретной вещи.
    List<BookingResponseDto> getBookingsForItem(Long itemId);
}