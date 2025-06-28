package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;


import java.util.List;

/**
 * Контроллер для управления операциями с предметами аренды.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;

    // Константа для имени заголовка
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    /**
     * Получение всех предметов пользователя
     * @param userId идентификатор пользователя
     * @return список DTO предметов
     */
    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItems(
            @RequestHeader(USER_ID_HEADER) Long userId) {
        return ResponseEntity.ok(itemService.getAllItems(userId));
    }

    // Метод для добавления новой вещи
    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @RequestBody ItemDto itemDto) {
        if (itemDto == null || itemDto.getName() == null || itemDto.getName().isEmpty() ||
                itemDto.getDescription() == null || itemDto.getDescription().isEmpty() ||
                itemDto.getAvailable() == null) {
            return ResponseEntity.badRequest().body(new ItemDto()); // 400
        }
        // Валидация пользователя через UserService вместо UserController
        if (userService.getUser(userId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(201).body(itemService.createItem(userId, itemDto));
    }

    // Метод для редактирования вещи
    /**
     * Обновление информации о предмете
     * @param userId идентификатор владельца
     * @param itemId идентификатор предмета
     * @param itemDto DTO с обновлёнными данными
     * @return обновлённый DTO предмета
     */
    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) {
        // Валидация пользователя через UserService
        if (userService.getUser(userId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ItemDto updatedItem = itemService.updateItem(userId, itemId, itemDto);
        return updatedItem != null ? ResponseEntity.ok(updatedItem) : ResponseEntity.notFound().build();
    }

    // Метод для получения информации о вещи по ID
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@RequestHeader(USER_ID_HEADER) Long userId, @PathVariable Long itemId) {
        // Валидация пользователя через UserService
        if (userService.getUser(userId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ItemDto item = itemService.getItem(userId, itemId);
        return item != null ? ResponseEntity.ok(item) : ResponseEntity.notFound().build();
    }

    // Метод для получения списка вещей владельца
    @GetMapping("/owner")
    public ResponseEntity<List<ItemDto>> getUserItems(@RequestHeader(USER_ID_HEADER) Long userId) {
        // Изменение: Валидация пользователя через UserService
        if (userService.getUser(userId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(itemService.getUserItems(userId));
    }

    // Метод для поиска вещей по тексту
    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestHeader(USER_ID_HEADER) Long userId,
                                                     @RequestParam String text) {
        if (userService.getUser(userId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(itemService.searchItems(userId, text));
    }

    /**
     * Создание комментария к предмету
     * @param userId идентификатор автора
     * @param itemId идентификатор предмета
     * @param commentDto DTO комментария
     * @return созданный комментарий
     */
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> createComment(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long itemId,
            @RequestBody CommentDto commentDto) {
        if (commentDto == null || commentDto.getText() == null || commentDto.getText().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (userService.getUser(userId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        CommentDto createdComment = itemService.createComment(userId, itemId, commentDto);
        return ResponseEntity.status(201).body(createdComment);
    }
}
