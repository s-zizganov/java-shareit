package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;


public interface ItemService {
    // Создание новой вещи
    ItemDto createItem(Long userId, ItemDto itemDto);

    // Обновление существующей вещи
    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    // Получение информации о вещи по ID
    ItemDto getItem(Long userId, Long itemId);

    // Получение списка вещей пользователя
    List<ItemDto> getUserItems(Long userId);

    // Поиск вещей по тексту
    List<ItemDto> searchItems(Long userId, String text);

    // Указываем, что метод возвращает ItemDto с заполненными датами бронирований
    List<ItemDto> getAllItems(Long userId);

    // Изменение: Добавлен метод для создания комментария
    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);
}
