package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class BookingResponseDto {
    /**
     * Идентификатор бронирования
     */
    Long id;
    /**
     * Дата и время начала бронирования
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime start;
    /**
     * Дата и время окончания бронирования
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
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

    // Конструктор по умолчанию (требуется для создания объекта без аргументов)
    public BookingResponseDto() {
    }
}