package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

// Класс для преобразования объектов User в UserDto и обратно
public class UserMapper {
    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    // Метод преобразует DTO обратно в сущность User
    public static User toUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }
}