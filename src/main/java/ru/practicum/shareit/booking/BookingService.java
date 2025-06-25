package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    BookingResponseDto createBooking(Long userId, BookingRequestDto bookingDto);
    BookingResponseDto approveBooking(Long userId, Long bookingId, boolean approved);
    Optional<BookingResponseDto> getBooking(Long userId, Long bookingId);
    List<BookingResponseDto> getBookings(Long userId, String state);
    List<BookingResponseDto> getOwnerBookings(Long userId, String state);
    // Изменение: Добавлен метод для получения бронирований по ID вещи
    List<BookingResponseDto> getBookingsForItem(Long itemId);
}