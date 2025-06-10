package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.CONFLICT;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private Long idCounter = 1L; // Счётчик для генерации уникальных id

    // Метод для создания нового пользователя
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto userDto) {
        // Проверяем, существует ли пользователь с таким же email
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(userDto.getEmail()))) {
            return ResponseEntity.status(CONFLICT).body(new ErrorResponse("Email already exists"));
        }
        User user = UserMapper.toUser(userDto);
        user.setId(idCounter++);
        users.put(user.getId(), user);
        return ResponseEntity.status(201).body(UserMapper.toUserDto(user));
    }

    // Метод для обновления данных пользователя
    @PatchMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        User user = users.get(userId);
        if (user == null) { // проверяем существует ли пользователь
            return ResponseEntity.notFound().build();
        }
        // Проверяем, не меняется ли email на существующий у другого пользователя
        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail()) &&
                users.values().stream().anyMatch(u -> u.getEmail().equals(userDto.getEmail()))) {
            return ResponseEntity.status(CONFLICT).body(new ErrorResponse("Email already exists"));
        }
        // Обновляем поля, если онии представлены в dto
        if (userDto.getName() != null) user.setName(userDto.getName());
        if (userDto.getEmail() != null) user.setEmail(userDto.getEmail());

        users.put(userId, user); // сохраняем обновленного пользователя

        return ResponseEntity.ok(UserMapper.toUserDto(user)); // возвращаем обновленного пользователя
    }

    // Метод для получения данных пользователя по id
    @GetMapping("/{userId}")
    public  ResponseEntity<UserDto> getUser(@PathVariable Long userId) {
        User user = users.get(userId);
        if (user == null) {
            return ResponseEntity.notFound().build(); // 404
        }
        return ResponseEntity.ok(UserMapper.toUserDto(user)); // Возвращаем в формате dto
    }

    // Метод для получения списка всех пользователей
    @GetMapping
    public ResponseEntity<Map<Long, UserDto>> getAllUsers() {
        Map<Long, UserDto> userDtos = new HashMap<>(); // создаем мапу для хранения пользователей

        // Преобразуем всех пользователей в dto и добавим в мапу
        for (Map.Entry<Long, User> entry : users.entrySet()) {
            userDtos.put(entry.getKey(), UserMapper.toUserDto(entry.getValue()));
        }
        return ResponseEntity.ok(userDtos);
    }

    // Метод для удаления пользователя
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        // Удаляем пользовтеля и проверим, был ли он удален
        if (users.remove(userId) != null) {
            return ResponseEntity.noContent().build(); // 204
        }
        return ResponseEntity.notFound().build(); // 404
    }

}
