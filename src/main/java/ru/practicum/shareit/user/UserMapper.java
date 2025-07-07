package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

/**
 * Утилитарный класс для преобразования объектов {@link User} в {@link UserDto} и обратно.
 */
public class UserMapper {
    /**
     * Преобразует объект {@link User} в {@link UserDto}.
     *
     * @param user объект сущности пользователя.
     * @return объект DTO пользователя.
     */
    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    /**
     * Преобразует объект {@link UserDto} в {@link User}.
     *
     * @param userDto объект DTO пользователя.
     * @return объект сущности пользователя.
     */
    public static User toUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }
}