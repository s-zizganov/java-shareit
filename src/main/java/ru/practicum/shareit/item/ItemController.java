package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {


    @Autowired
    private UserController userController; // Инъекция UserController

    @Autowired
    private ItemService itemService; // Инъекция ItemService

    // Метод для добавления новой вещи
    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestBody ItemDto itemDto) {
        if (itemDto == null || itemDto.getName() == null || itemDto.getName().isEmpty() ||
                itemDto.getDescription() == null || itemDto.getDescription().isEmpty() ||
                itemDto.getAvailable() == null) {
            return ResponseEntity.badRequest().body(new ItemDto()); // 400
        }
        if (!isValidUser(userId)) {
            return ResponseEntity.notFound().build(); // 404
        }
        return ResponseEntity.status(201).body(itemService.createItem(userId, itemDto));
    }

    // Метод для редактирования вещи
    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        if (!isValidUser(userId)) {
            return ResponseEntity.notFound().build(); // 404, если пользователь не найден
        }
        ItemDto updatedItem = itemService.updateItem(userId, itemId, itemDto);
        return updatedItem != null ? ResponseEntity.ok(updatedItem) : ResponseEntity.notFound().build();
    }

    // Метод для получения информации о вещи по ID
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable Long itemId) {
        ItemDto item = itemService.getItem(itemId);
        return item != null ? ResponseEntity.ok(item) : ResponseEntity.notFound().build();
    }

    // Метод для получения списка вещей владельца
    @GetMapping
    public ResponseEntity<List<ItemDto>> getUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        if (!isValidUser(userId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(itemService.getUserItems(userId));
    }

    // Метод для поиска вещей по тексту
    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam String text) {
        if (!isValidUser(userId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(itemService.searchItems(userId, text));
    }

    // Вспомогательный метод для проверки существования пользователя
    private boolean isValidUser(Long userId) {
        ResponseEntity<UserDto> response = userController.getUser(userId);
        return response.getStatusCode().is2xxSuccessful();
    }

}
