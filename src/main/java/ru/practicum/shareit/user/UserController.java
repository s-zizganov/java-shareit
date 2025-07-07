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
 * REST-контроллер для управления пользователями.
 * Предоставляет эндпоинты для создания, обновления, получения и удаления пользователей.
 */
@RestController
@RequestMapping(path = "/users")
// Добавлена аннотация @RequiredArgsConstructor для инъекции UserService
@RequiredArgsConstructor
@Slf4j
public class UserController {
    /**
     * Сервис для работы с пользователями.
     */
    private final UserService userService;

    /**
     * Создает нового пользователя.
     *
     * @param userDto DTO с данными для создания пользователя.
     * @return {@link ResponseEntity} с DTO созданного пользователя и статусом 201.
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserDto userDto) {
        log.info("Received request to create user: {}", userDto);
        UserDto createdUser = userService.createUser(userDto);
        log.info("User created successfully: {}", createdUser);
        return ResponseEntity.status(201).body(createdUser);
    }

    /**
     * Обновляет данные существующего пользователя.
     *
     * @param userId  Идентификатор пользователя для обновления.
     * @param userDto DTO с новыми данными пользователя.
     * @return {@link ResponseEntity} с DTO обновленного пользователя и статусом 200.
     *         В случае конфликта (например, дубликат email) возвращает статус 409.
     *         В случае других ошибок — соответствующий статус ошибки.
     */
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

    /**
     * Получает пользователя по его идентификатору.
     *
     * @param userId Идентификатор пользователя.
     * @return {@link ResponseEntity} с DTO пользователя и статусом 200, если пользователь найден.
     *         В противном случае возвращает статус 404.
     */
    @GetMapping("/{userId}")
    public  ResponseEntity<UserDto> getUser(@PathVariable Long userId) {
        // Изменение: Использование userService.getUser вместо получения из Map
        return userService.getUser(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return {@link ResponseEntity} со списком DTO всех пользователей и статусом 200.
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param userId Идентификатор удаляемого пользователя.
     * @return {@link ResponseEntity} со статусом 204 в случае успешного удаления.
     *         В случае, если пользователь не найден, возвращает статус 404.
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
