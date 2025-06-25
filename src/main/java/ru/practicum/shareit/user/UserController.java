package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.springframework.http.HttpStatus.CONFLICT;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
// Добавлена аннотация @RequiredArgsConstructor для инъекции UserService
@RequiredArgsConstructor
@Slf4j
public class UserController {
    // Убран Map<Long, User> и idCounter, добавлен UserService для работы с базой данных
    private final UserService userService;

    private Long idCounter = 1L; // Счётчик для генерации уникальных id

    // Метод для создания нового пользователя
    @PostMapping

    public ResponseEntity<?> createUser(@RequestBody UserDto userDto) {
        log.info("Received request to create user: {}", userDto);
        UserDto createdUser = userService.createUser(userDto);
        log.info("User created successfully: {}", createdUser);
        return ResponseEntity.status(201).body(createdUser);
    }

    // Метод для обновления данных пользователя
    @PatchMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        try {
            return ResponseEntity.ok(userService.updateUser(userId, userDto));
        } catch (RuntimeException e) {
            if (e instanceof ConflictException) {
                return ResponseEntity.status(CONFLICT).body(new ErrorResponse(e.getMessage()));
            }
            return userService.getUser(userId)
                    .map(user -> ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage())))
                    .orElse(ResponseEntity.notFound().build());
        }
    }

    // Метод для получения данных пользователя по id
    @GetMapping("/{userId}")
    public  ResponseEntity<UserDto> getUser(@PathVariable Long userId) {
        // Изменение: Использование userService.getUser вместо получения из Map
        return userService.getUser(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Метод для получения списка всех пользователей
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        // Изменение: Использование userService.getAllUsers вместо итерации по Map
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Метод для удаления пользователя
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        // Изменение: Использование userService.deleteUser вместо удаления из Map
        try {
            userService.deleteUser(userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
