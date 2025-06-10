package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

// Аннотация @Data генерирует геттеры, сеттеры, toString, equals и hashCode
@Data
public class BookingDto {
    // Поле id — идентификатор бронирования
    private Long id;

    // Поле start — дата и время начала бронирования
    private LocalDateTime start;

    // Поле end — дата и время окончания бронирования
    private LocalDateTime end;

    // Поле itemId — идентификатор вещи
    private Long itemId;

    // Поле bookerId — идентификатор пользователя, сделавшего бронирование
    private Long bookerId;

    // Поле status — статус бронирования
    private String status;
}