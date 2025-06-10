package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    // Метод преобразует сущность Item в DTO для передачи через REST API
    public static ItemDto toItemDto(Item item) {
        // Создаём новый объект ItemDto
        ItemDto itemDto = new ItemDto();
        // Копируем идентификатор
        itemDto.setId(item.getId());
        // Копируем название
        itemDto.setName(item.getName());
        // Копируем описание
        itemDto.setDescription(item.getDescription());
        // Копируем доступность
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRentalCount(0);
        return itemDto;
    }

    // Метод преобразует DTO обратно в сущность Item
    public static Item toItem(ItemDto itemDto) {
        // Создаём новый объект Item
        Item item = new Item();
        // Копируем идентификатор
        item.setId(itemDto.getId());
        // Копируем название
        item.setName(itemDto.getName());
        // Копируем описание
        item.setDescription(itemDto.getDescription());
        // Копируем доступность
        item.setAvailable(itemDto.getAvailable());
        // Устанавливаем ownerId из DTO
        item.setOwnerId(itemDto.getOwnerId());
        return item;
    }
}