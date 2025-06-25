package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с пользователями.
 */
public interface UserService {
    /**
     * Создает нового пользователя.
     *
     * @param userDto данные нового пользователя.
     * @return созданный пользователь.
     */
    UserDto createUser(UserDto userDto);

    /**
     * Обновляет данные существующего пользователя.
     *
     * @param userId  идентификатор пользователя для обновления.
     * @param userDto объект с новыми данными пользователя.
     * @return обновленный пользователь.
     */
    UserDto updateUser(Long userId, UserDto userDto);

    /**
     * Получает пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя.
     * @return {@link Optional} с пользователем, если найден, иначе пустой.
     */
    Optional<UserDto> getUser(Long userId);

    /**
     * Возвращает список всех пользователей.
     *
     * @return список всех пользователей.
     */
    List<UserDto> getAllUsers();

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param userId идентификатор удаляемого пользователя.
     */
    void deleteUser(Long userId);
}