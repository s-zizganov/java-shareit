package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с бронированиями.
 * Предоставляет методы для создания, подтверждения, получения и поиска бронирований.
 */
public interface BookingService {
    /**
     * Создает новое бронирование.
     * @param userId     идентификатор пользователя, создающего бронирование
     * @param bookingDto данные для создания бронирования
     * @return информация о созданном бронировании
     */
    BookingResponseDto createBooking(Long userId, BookingRequestDto bookingDto);

    /**
     * Подтверждает или отклоняет бронирование.
     * @param userId    идентификатор пользователя (владельца вещи)
     * @param bookingId идентификатор бронирования
     * @param approved  флаг подтверждения (true - подтвердить, false - отклонить)
     * @return обновленная информация о бронировании
     */
    BookingResponseDto approveBooking(Long userId, Long bookingId, boolean approved);
    
    /**
     * Получает информацию о бронировании по его идентификатору.
     * @param userId    идентификатор пользователя (владельца или автора бронирования)
     * @param bookingId идентификатор бронирования
     * @return информация о бронировании или пустой Optional, если бронирование не найдено
     */
    Optional<BookingResponseDto> getBooking(Long userId, Long bookingId);

    /**
     * Получает список бронирований пользователя.
     * @param userId идентификатор пользователя
     * @param state  состояние бронирований для фильтрации (ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED)
     * @return список бронирований пользователя
     */
    List<BookingResponseDto> getBookings(Long userId, String state);

    /**
     * Получает список бронирований для вещей, принадлежащих пользователю.
     * @param userId идентификатор пользователя-владельца вещей
     * @param state  состояние бронирований для фильтрации (ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED)
     * @return список бронирований вещей, принадлежащих пользователю
     */
    List<BookingResponseDto> getOwnerBookings(Long userId, String state);

    /**
     * Получает список бронирований для конкретной вещи.
     * @param itemId идентификатор вещи
     * @return список бронирований для указанной вещи
     */
    List<BookingResponseDto> getBookingsForItem(Long itemId);
}