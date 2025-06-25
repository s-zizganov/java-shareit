package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Value;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

/**
 * DTO для запроса вещи.
 */
@Value
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestDto {
    /**
     * Уникальный идентификатор запроса.
     */
    Long id;
    /**
     * Текст запроса, содержащий описание требуемой вещи.
     */
    String description;
    /**
     * Дата и время создания запроса.
     */
    LocalDateTime created;
    /**
     * Идентификатор пользователя, создавшего запрос.
     */
    Long requesterId;
}
