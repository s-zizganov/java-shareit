package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Data
// Аннотация @Entity указывает, что класс является сущностью для базы данных
@Entity
// Аннотация @Table задаёт имя таблицы в базе данных
@Table(name = "bookings")
public class Booking {
    // Поле id — уникальный идентификатор бронирования
    @Id
    // Автоматическая генерация значения id (инкремент)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Поле start — дата и время начала бронирования, не может быть null
    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;

    // Поле end — дата и время окончания бронирования, не может быть null
    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;

    // Поле itemId — идентификатор вещи, не может быть null
    @Column(name = "item_id", nullable = false)
    private Long itemId;

    // Поле bookerId — идентификатор пользователя, сделавшего бронирование
    @Column(name = "booker_id", nullable = false)
    private Long bookerId;

    // Поле status — статус бронирования (например, WAITING, APPROVED, REJECTED)
    @Column(nullable = false)
    private String status;
}