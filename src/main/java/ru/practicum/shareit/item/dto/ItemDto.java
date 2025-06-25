package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    Long id;
    String name;
    String description;
    Boolean available;
    Integer rentalCount;
    Long ownerId;

    // Добавлены поля для дат последнего и ближайшего бронирования
    LocalDateTime lastBooking; // Дата и время последнего бронирования
    LocalDateTime nextBooking; // Дата и время ближайшего следующего бронирования

    // Изменение: Добавлено поле для списка комментариев
    List<CommentDto> comments;
}
