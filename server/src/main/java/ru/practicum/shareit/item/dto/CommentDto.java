package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

/**
 * DTO для комментариев к предметам.
 * Содержит информацию о комментарии, его авторе и времени создания.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
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

    // Конструктор по умолчанию (требуется для некоторых фреймворков)
    public CommentDto() {
    }

    // Конструктор с минимальными полями
    public CommentDto(Long id, String text, Long itemId) {
        this.id = id;
        this.text = text;
        this.itemId = itemId;
        this.authorId = null; // Дефолтное значение
        this.authorName = null; // Дефолтное значение
        this.created = null; // Дефолтное значение, будет установлено позже сервисом
    }
}