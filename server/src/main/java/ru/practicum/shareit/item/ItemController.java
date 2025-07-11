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
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    /**
     * Получение всех предметов пользователя.
     * @param userId идентификатор пользователя
     * @return список DTO предметов
     */
    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItems(@RequestHeader(USER_ID_HEADER) Long userId) {
        return ResponseEntity.ok(itemService.getAllItems(userId));
    }

    /**
     * Создание новой вещи.
     * @param userId  идентификатор пользователя
     * @param itemDto DTO с данными вещи
     * @return созданная вещь
     */
    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok(itemService.createItem(userId, itemDto));
    }

    /**
     * Обновление информации о предмете.
     * @param userId  идентификатор владельца
     * @param itemId  идентификатор предмета
     * @param itemDto DTO с обновлёнными данными
     * @return обновлённый DTO предмета
     */
    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @PathVariable Long itemId,
                                              @RequestBody ItemDto itemDto) {
        ItemDto updatedItem = itemService.updateItem(userId, itemId, itemDto);
        return ResponseEntity.ok(updatedItem);
    }

    /**
     * Получение информации о вещи по ID.
     * @param userId идентификатор пользователя
     * @param itemId идентификатор вещи
     * @return информация о вещи
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                           @PathVariable Long itemId) {
        ItemDto item = itemService.getItem(userId, itemId);
        return ResponseEntity.ok(item);
    }

    /**
     * Получение списка вещей владельца.
     * @param userId идентификатор пользователя
     * @return список вещей пользователя
     */
    @GetMapping("/owner")
    public ResponseEntity<List<ItemDto>> getUserItems(@RequestHeader(USER_ID_HEADER) Long userId) {
        return ResponseEntity.ok(itemService.getUserItems(userId));
    }

    /**
     * Поиск вещей по тексту.
     * @param userId идентификатор пользователя
     * @param text   текст для поиска
     * @return список найденных вещей
     */
    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestHeader(USER_ID_HEADER) Long userId,
                                                     @RequestParam String text) {
        return ResponseEntity.ok(itemService.searchItems(userId, text));
    }

    /**
     * Создание комментария к предмету.
     * @param userId     идентификатор автора
     * @param itemId     идентификатор предмета
     * @param commentDto DTO комментария
     * @return созданный комментарий
     */
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> createComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                                    @PathVariable Long itemId,
                                                    @RequestBody CommentDto commentDto) {
        if (commentDto == null || commentDto.getText() == null || commentDto.getText().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        CommentDto createdComment = itemService.createComment(userId, itemId, commentDto);
        return ResponseEntity.status(201).body(createdComment);
    }
}