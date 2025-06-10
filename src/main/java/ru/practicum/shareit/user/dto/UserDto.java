package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDto {
    // Поле id — идентификатор пользователя
    private Long id;

    // Поле name — имя пользователя
    @NotNull(message = "Name cannot be null")
    private String name;

    // Поле email — электронная почта пользователя
    @NotNull(message = "Email cannot be null")
    @Email(message = "Email should be valid")
    private String email;
}