package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;


public interface ItemService {
    /**
     * Создание новой вещи.
     *
     * @param userId  идентификатор пользователя, создающего вещь.
     * @param itemDto объект с данными о вещи.
     * @return созданная вещь.
     */
    ItemDto createItem(Long userId, ItemDto itemDto);

    /**
     * Обновление существующей вещи.
     *
     * @param userId  идентификатор пользователя, обновляющего вещь.
     * @param itemId  идентификатор вещи для обновления.
     * @param itemDto объект с новыми данными о вещи.
     * @return обновленная вещь.
     */
    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    /**
     * Получение информации о вещи по ID.
     *
     * @param userId идентификатор пользователя, запрашивающего вещь.
     * @param itemId идентификатор вещи.
     * @return информация о вещи.
     */
    ItemDto getItem(Long userId, Long itemId);

    /**
     * Получение списка всех вещей пользователя.
     *
     * @param userId идентификатор пользователя.
     * @return список вещей пользователя.
     */
    List<ItemDto> getUserItems(Long userId);

    /**
     * Поиск вещей по текстовому запросу.
     *
     * @param userId идентификатор пользователя, выполняющего поиск.
     * @param text   текст для поиска в названии или описании вещи.
     * @return список найденных вещей.
     */
    List<ItemDto> searchItems(Long userId, String text);

    /**
     * Получение списка всех вещей с информацией о бронированиях.
     *
     * @param userId идентификатор пользователя, запрашивающего список.
     * @return список всех вещей.
     */
    List<ItemDto> getAllItems(Long userId);

    /**
     * Создание нового комментария к вещи.
     *
     * @param userId     идентификатор пользователя, оставляющего комментарий.
     * @param itemId     идентификатор вещи, к которой оставляют комментарий.
     * @param commentDto объект с данными комментария.
     * @return созданный комментарий.
     */
    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);
}
