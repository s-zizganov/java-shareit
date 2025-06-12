package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

// Аннотация @Data генерирует геттеры, сеттеры, toString, equals и hashCode
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {
    // Поле id — идентификатор бронирования
    Long id;

    // Поле start — дата и время начала бронирования
    LocalDateTime start;

    // Поле end — дата и время окончания бронирования
    LocalDateTime end;

    // Поле itemId — идентификатор вещи
    Long itemId;

    // Поле bookerId — идентификатор пользователя, сделавшего бронирование
    Long bookerId;

    // Поле status — статус бронирования
    String status;
}