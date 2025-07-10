package ru.practicum.shareit.request;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<Item> items) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemRequest.getRequesterId(),
                items != null ? items.stream()
                        .map(item -> new ItemRequestDto.ItemResponse(
                                item.getId(),
                                item.getName(),
                                item.getOwnerId()
                        ))
                        .collect(Collectors.toList()) : null
        );
    }

    public static ItemRequest toItemRequest(ItemRequestDto dto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(dto.getId());
        itemRequest.setDescription(dto.getDescription());
        itemRequest.setRequesterId(dto.getRequesterId());
        itemRequest.setCreated(dto.getCreated());
        return itemRequest;
    }
}