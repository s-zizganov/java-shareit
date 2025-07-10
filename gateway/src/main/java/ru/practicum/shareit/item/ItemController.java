package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * Контроллер для управления операциями с предметами аренды в gateway.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.getAllItems(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @Valid @RequestBody ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new IllegalArgumentException("Название вещи не может быть пустым");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new IllegalArgumentException("Описание вещи не может быть пустым");
        }
        if (itemDto.getAvailable() == null) {
            throw new IllegalArgumentException("Статус доступности вещи должен быть указан");
        }
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                          @PathVariable Long itemId) {
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getUserItems(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.getUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @RequestParam @NotBlank String text) {
        return itemClient.searchItems(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                                @PathVariable Long itemId,
                                                @Valid @RequestBody CommentDto commentDto) {
        return itemClient.createComment(userId, itemId, commentDto);
    }
}