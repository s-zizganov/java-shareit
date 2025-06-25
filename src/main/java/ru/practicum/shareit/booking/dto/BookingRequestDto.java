package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

import java.time.LocalDateTime;

/**
 * DTO для запроса на создание бронирования
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingRequestDto {
    /**
     * Дата и время начала бронирования
     */
    @NotNull(message = "Дата начала не может быть пустой")
    LocalDateTime start;

    /**
     * Дата и время окончания бронирования
     */
    @NotNull(message = "Дата окончания не может быть пустой")
    LocalDateTime end;

    /**
     * Идентификатор вещи для бронирования
     */
    @NotNull(message = "Идентификатор вещи не может быть пустым")
    @Positive(message = "Идентификатор вещи должен быть положительным")
    Long itemId;
}