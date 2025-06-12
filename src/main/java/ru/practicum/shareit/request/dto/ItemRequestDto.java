package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Value;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Value
@FieldDefaults(level = AccessLevel.PRIVATE)
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
