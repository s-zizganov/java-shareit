package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

/**
 * DTO для комментариев к предметам.
 * Содержит информацию о комментарии, его авторе и времени создания.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
    /**
     * Уникальный идентификатор комментария.
     */
    Long id;

    /**
     * Текст комментария.
     */
    String text;

    /**
     * Идентификатор предмета, к которому относится комментарий.
     */
    Long itemId;

    /**
     * Идентификатор автора комментария.
     */
    Long authorId;

    /**
     * Имя автора для отображения.
     */
    String authorName;

    /**
     * Время создания комментария.
     */
    LocalDateTime created;
}