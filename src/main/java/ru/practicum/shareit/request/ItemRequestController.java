package ru.practicum.shareit.request;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * REST-контроллер для обработки запросов на вещи.
 * Предоставляет эндпоинты для создания и получения запросов.
 */
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    /**
     * Хранилище запросов в памяти, где ключ — ID запроса.
     */
    private final Map<Long, ItemRequest> requests = new HashMap<>();
    /**
     * Счётчик для генерации уникальных ID запросов.
     */
    private Long idCounter = 1L;
    /**
     * Константа для имени заголовка с ID пользователя.
     */
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    /**
     * Создает новый запрос на вещь.
     *
     * @param userId     Идентификатор пользователя, создающего запрос. Передаётся в заголовке.
     * @param requestDto DTO с данными для создания запроса.
     * @return {@link ResponseEntity} с DTO созданного запроса и статусом 201.
     *         В случае некорректных данных возвращает статус 400.
     */
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

    /**
     * Возвращает список запросов, созданных пользователем.
     *
     * @param userId Идентификатор пользователя, чьи запросы нужно получить.
     * @return {@link ResponseEntity} с картой запросов пользователя и статусом 200.
     */
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