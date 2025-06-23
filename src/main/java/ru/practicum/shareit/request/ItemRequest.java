package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

// Аннотация @Entity указывает, что класс является сущностью для базы данных
@Entity
// Аннотация @Table задаёт имя таблицы в базе данных
@Table(name = "requests")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequest {
    // Поле id — уникальный идентификатор запроса
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // Поле description — описание запроса, не может быть null
    @Column(nullable = false)
    String description;

    // Поле requesterId — идентификатор пользователя, создавшего запрос
    @Column(name = "requester_id", nullable = false)
    Long requesterId;

    // Поле created — дата и время создания запроса, не может быть null
    @Column(nullable = false)
    LocalDateTime created;
}