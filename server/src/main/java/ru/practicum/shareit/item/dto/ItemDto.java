package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO для предмета.
 * Содержит информацию о предмете, его доступности, владельце, бронированиях и комментариях.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ItemDto {
    /**
     * Уникальный идентификатор предмета.
     */
    Long id;

    /**
     * Название предмета.
     */
    String name;

    /**
     * Описание предмета.
     */
    String description;

    /**
     * Доступность предмета для бронирования.
     */
    Boolean available;

    /**
     * Количество раз, когда предмет был арендован.
     */
    Integer rentalCount;

    /**
     * Идентификатор владельца предмета.
     */
    Long ownerId;

    /**
     * Время последнего бронирования предмета.
     */
    LocalDateTime lastBooking;

    /**
     * Время следующего бронирования предмета.
     */
    LocalDateTime nextBooking;

    /**
     * Список комментариев к предмету.
     */
    List<CommentDto> comments;

    /**
     * Идентификатор запроса, связанного с предметом (необязательное поле).
     */
    Long requestId;

    public ItemDto() {
    }
}
