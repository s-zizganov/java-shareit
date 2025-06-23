package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemServiceImpl implements ItemService {
    // Хранилище вещей в памяти, ключ — ID вещи
    private final Map<Long, Item> items = new HashMap<>();
    // Хранилище соответствия вещей и их владельцев
    private final Map<Long, Long> itemOwners = new HashMap<>(); // itemId -> ownerId
    private Long idCounter = 1L;

    // Реализация метода создания вещи
    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        item.setId(idCounter++);
        item.setOwnerId(userId); // Устанавливаем ownerId из заголовка
        items.put(item.getId(), item);
        itemOwners.put(item.getId(), userId);
        return ItemMapper.toItemDto(item);
    }

    // Реализация метода обновления вещи
    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        // Получаем вещь по ID
        Item item = items.get(itemId);
        // Проверяем, существует ли вещь и принадлежит ли она пользователю
        if (item == null || !item.getOwnerId().equals(userId)) {
            return null;
        }
        // Обновляем поля, если они предоставлены
        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());
        // Сохраняем обновлённую вещь
        items.put(itemId, item);
        // Возвращаем обновлённую вещь
        return ItemMapper.toItemDto(item);
    }

    // Реализация метода получения вещи по ID
    @Override
    public ItemDto getItem(Long itemId) {
        // Получаем вещь по ID
        Item item = items.get(itemId);
        // Возвращаем null, если вещь не найдена, иначе — DTO
        return item != null ? ItemMapper.toItemDto(item) : null;
    }

    // Реализация метода получения списка вещей пользователя
    @Override
    public List<ItemDto> getUserItems(Long userId) {
        // Создаём список для хранения DTO вещей пользователя
        List<ItemDto> userItems = new ArrayList<>();
        // Фильтруем вещи по владельцу
        for (Map.Entry<Long, Item> entry : items.entrySet()) {
            if (entry.getValue().getOwnerId().equals(userId)) {
                userItems.add(ItemMapper.toItemDto(entry.getValue()));
            }
        }
        // Возвращаем список вещей
        return userItems;
    }

    // Реализация метода поиска вещей по тексту
    @Override
    public List<ItemDto> searchItems(Long userId, String text) {
        // Создаём список для хранения найденных вещей
        List<ItemDto> foundItems = new ArrayList<>();
        // Проверяем, пустой ли текст поиска
        if (text == null || text.isEmpty()) {
            return foundItems; // Пустой список, если текст отсутствует
        }
        // Ищем вещи по названию или описанию среди доступных
        for (Item item : items.values()) {
            if (item.getAvailable() != null && item.getAvailable() &&
                    (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                            item.getDescription().toLowerCase().contains(text.toLowerCase()))) {
                foundItems.add(ItemMapper.toItemDto(item));
            }
        }
        // Возвращаем найденные вещи
        return foundItems;
    }
}