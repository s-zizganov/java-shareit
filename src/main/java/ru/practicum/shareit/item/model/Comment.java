package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "text", nullable = false, length = 1000)
    String text;

    @Column(name = "item_id", nullable = false)
    Long itemId;

    @Column(name = "author_id", nullable = false)
    Long authorId;

    @Column(name = "created", nullable = false)
    LocalDateTime created;
}