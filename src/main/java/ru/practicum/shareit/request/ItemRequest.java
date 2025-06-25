package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

/**
 * Класс, представляющий запрос на вещь.
 * Используется для хранения информации о запросах в базе данных.
 */
@Entity
// Аннотация @Table задаёт имя таблицы в базе данных
@Table(name = "requests")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequest {
    /**
     * Уникальный идентификатор запроса.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /**
     * Текст запроса, содержащий описание требуемой вещи.
     */
    @Column(nullable = false)
    String description;

    /**
     * Идентификатор пользователя, создавшего запрос.
     */
    @Column(name = "requester_id", nullable = false)
    Long requesterId;

    /**
     * Дата и время создания запроса.
     */
    @Column(nullable = false)
    LocalDateTime created;
}