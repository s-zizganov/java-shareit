package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Модель предмета для аренды.
 */
@Entity
@Table(name = "items")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    /**
     * Уникальный идентификатор предмета
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /**
     * Название предмета
     */
    @Column(nullable = false)
    String name;

    /**
     * Описание предмета
     */
    @Column(nullable = false)
    String description;

    /**
     * Доступность предмета для бронирования
     */
    @Column(name = "is_available", nullable = false)
    Boolean available;

    /**
     * Идентификатор владельца предмета
     */
    @Column(name = "owner_id", nullable = false)
    Long ownerId;

    /**
     * Количество аренд предмета
     */
    @Column(name = "rental_count")
    Integer rentalCount;

    /**
     * Идентификатор запроса, связанного с предметом
     */
    @Column(name = "request_id")
    Long requestId;
}