package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

// Аннотация @Entity указывает, что класс является сущностью для базы данных
@Entity
// Аннотация @Table задаёт имя таблицы в базе данных
@Table(name = "requests")
@Data
public class ItemRequest {
    // Поле id — уникальный идентификатор запроса
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Поле description — описание запроса, не может быть null
    @Column(nullable = false)
    private String description;

    // Поле requesterId — идентификатор пользователя, создавшего запрос
    @Column(name = "requester_id", nullable = false)
    private Long requesterId;

    // Поле created — дата и время создания запроса, не может быть null
    @Column(nullable = false)
    private LocalDateTime created;
}