package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

/**
 * Сущность комментария к предмету.
 * Содержит текст комментария, информацию об авторе, предмете и времени создания.
 */
@Entity
@Table(name = "comments")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    /**
     * Уникальный идентификатор комментария.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /**
     * Текст комментария.
     * Максимальная длина 1000 символов.
     */
    @Column(name = "text", nullable = false, length = 1000)
    String text;

    /**
     * Идентификатор предмета, к которому относится комментарий.
     */
    @Column(name = "item_id", nullable = false)
    Long itemId;

    /**
     * Идентификатор автора комментария.
     */
    @Column(name = "author_id", nullable = false)
    Long authorId;

    /**
     * Время создания комментария.
     */
    @Column(name = "created", nullable = false)
    LocalDateTime created;
}