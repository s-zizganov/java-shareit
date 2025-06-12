package ru.practicum.shareit.request;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// Аннотация @RestController указывает, что это REST-контроллер
@RestController
// Базовый путь для всех эндпоинтов этого контроллера
@RequestMapping(path = "/requests")
public class ItemRequestController {
    // Хранилище запросов в памяти, ключ — ID запроса
    private final Map<Long, ItemRequest> requests = new HashMap<>();
    // Счётчик для генерации уникальных ID
    private Long idCounter = 1L;
    // Константа для имени заголовка
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    // Метод для создания нового запроса на вещь
    @PostMapping
    public ResponseEntity<ItemRequestDto> createRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                                        @RequestBody ItemRequestDto requestDto) {
        // Проверяем, что описание предоставлено
        if (requestDto.getDescription() == null || requestDto.getDescription().isEmpty()) {
            return ResponseEntity.badRequest().build(); // 400, если описание пустое
        }
        // Создаём новую сущность запроса
        ItemRequest request = new ItemRequest();
        request.setId(idCounter++);
        request.setDescription(requestDto.getDescription());
        request.setRequesterId(userId);
        request.setCreated(LocalDateTime.now());
        // Сохраняем запрос
        requests.put(request.getId(), request);
        // Возвращаем DTO с кодом 201
        return ResponseEntity.status(201).body(new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                request.getRequesterId()
        ));
    }

    // Метод для получения списка запросов пользователя
    @GetMapping
    public ResponseEntity<Map<Long, ItemRequestDto>> getUserRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        // Создаём мапу для хранения DTO запросов
        Map<Long, ItemRequestDto> userRequests = new HashMap<>();
        for (Map.Entry<Long, ItemRequest> entry : requests.entrySet()) {
            if (entry.getValue().getRequesterId().equals(userId)) {
                ItemRequest request = entry.getValue();
                userRequests.put(request.getId(), new ItemRequestDto(
                        request.getId(),
                        request.getDescription(),
                        request.getCreated(),
                        request.getRequesterId()
                ));
            }
        }
        // Возвращаем список запросов пользователя
        return ResponseEntity.ok(userRequests);
    }
}