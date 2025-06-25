package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

// Изменение: Создан интерфейс UserService для определения контракта сервиса пользователей
public interface UserService {
    // Метод для создания нового пользователя
    UserDto createUser(UserDto userDto);

    // Метод для обновления данных пользователя
    UserDto updateUser(Long userId, UserDto userDto);

    // Метод для получения данных пользователя по ID
    Optional<UserDto> getUser(Long userId);

    // Метод для получения списка всех пользователей
    List<UserDto> getAllUsers();

    // Метод для удаления пользователя
    void deleteUser(Long userId);
}