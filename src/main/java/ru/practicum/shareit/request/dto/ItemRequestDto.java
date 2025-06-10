package ru.practicum.shareit.request.dto;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class ItemRequestDto {
    // Уникальный идентификатор запроса
    Long id;
    // Описание запроса
    String description;
    // Дата и время создания запроса
    LocalDateTime created;
    // Идентификатор пользователя, создавшего запрос
    Long requesterId;
}
