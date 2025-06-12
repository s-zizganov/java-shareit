package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    // Поле id — идентификатор пользователя
    Long id;

    // Поле name — имя пользователя
    @NotNull(message = "Name cannot be null")
    String name;

    // Поле email — электронная почта пользователя
    @NotNull(message = "Email cannot be null")
    @Email(message = "Email should be valid")
    String email;
}