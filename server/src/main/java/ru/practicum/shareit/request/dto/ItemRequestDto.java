package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO для запроса вещи.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    /**
     * Уникальный идентификатор запроса.
     */
    Long id;
    /**
     * Текст запроса, содержащий описание требуемой вещи.
     */
    @NotBlank
    String description;
    /**
     * Дата и время создания запроса.
     */
    LocalDateTime created;
    /**
     * Идентификатор пользователя, создавшего запрос.
     */
    Long requesterId;
    /**
     * Список вещей, добавленных в ответ на запрос.
     */
    List<ItemResponse> items;

    // Конструктор с 3 аргументами для теста
    public ItemRequestDto(Long id, String description, LocalDateTime created) {
        this.id = id;
        this.description = description;
        this.created = created;
        this.requesterId = null; // Будет установлено сервером
        this.items = List.of(); // Пустой список
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemResponse {
        Long id;
        String name;
        Long ownerId;
    }
}