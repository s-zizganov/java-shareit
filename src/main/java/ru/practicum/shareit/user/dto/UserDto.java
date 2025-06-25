package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * DTO для пользователя.
 * Используется для передачи данных о пользователе между слоями приложения.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    /**
     * Уникальный идентификатор пользователя.
     */
    Long id;

    /**
     * Имя пользователя. Не может быть null.
     */
    @NotNull(message = "Name cannot be null")
    String name;

    /**
     * Электронная почта пользователя. Должна быть в корректном формате и не может быть null.
     */
    @NotNull(message = "Email cannot be null")
    @Email(message = "Email should be valid")
    String email;
}