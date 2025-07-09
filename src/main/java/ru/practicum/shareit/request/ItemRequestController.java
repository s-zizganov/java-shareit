package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * REST-контроллер для обработки запросов на вещи.
 * Предоставляет эндпоинты для создания и получения запросов.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    /**
     * Создает новый запрос на вещь.
     *
     * @param userId     Идентификатор пользователя, создающего запрос.
     * @param requestDto DTO с данными для создания запроса.
     * @return {@link ResponseEntity} с DTO созданного запроса и статусом 200.
     */
    @PostMapping
    public ResponseEntity<ItemRequestDto> createRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                                        @RequestBody ItemRequestDto requestDto) {
        return ResponseEntity.ok(itemRequestService.createRequest(userId, requestDto));
    }

    /**
     * Возвращает список запросов, созданных пользователем.
     *
     * @param userId Идентификатор пользователя, чьи запросы нужно получить.
     * @return {@link ResponseEntity} с списком запросов пользователя и статусом 200.
     */
    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getUserRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        return ResponseEntity.ok(itemRequestService.getUserRequests(userId));
    }

    /**
     * Возвращает список запросов, созданных другими пользователями.
     *
     * @param userId Идентификатор пользователя, запрашивающего список.
     * @return {@link ResponseEntity} с списком запросов и статусом 200.
     */
    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        return ResponseEntity.ok(itemRequestService.getAllRequests(userId));
    }

    /**
     * Возвращает данные о конкретном запросе.
     *
     * @param userId    Идентификатор пользователя, запрашивающего данные.
     * @param requestId Идентификатор запроса.
     * @return {@link ResponseEntity} с DTO запроса и статусом 200.
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getRequestById(@RequestHeader(USER_ID_HEADER) Long userId,
                                                         @PathVariable Long requestId) {
        return ResponseEntity.ok(itemRequestService.getRequestById(userId, requestId));
    }
}