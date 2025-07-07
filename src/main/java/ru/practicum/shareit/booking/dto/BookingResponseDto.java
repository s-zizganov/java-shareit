package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

/**
 * DTO для ответа с информацией о бронировании
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingResponseDto {
    /**
     * Идентификатор бронирования
     */
    Long id;
    /**
     * Дата и время начала бронирования
     */
    LocalDateTime start;
    /**
     * Дата и время окончания бронирования
     */
    LocalDateTime end;
    /**
     * Информация о забронированной вещи
     */
    ItemDto item;
    /**
     * Информация о пользователе, создавшем бронирование
     */
    UserDto booker;
    /**
     * Статус бронирования (например, WAITING, APPROVED, REJECTED, CANCELED)
     */
    String status;
}