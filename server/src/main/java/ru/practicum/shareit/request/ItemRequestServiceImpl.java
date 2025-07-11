package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto) {
        log.info("Creating item request for user ID {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с ID %d не найден", userId));
        }
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequesterId(userId);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestDto(savedRequest, null);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с ID %d не найден", userId));
        }
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId);
        return requests.stream()
                .map(request -> {
                    List<Item> items = itemRepository.findByRequestId(request.getId());
                    return ItemRequestMapper.toItemRequestDto(request, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с ID %d не найден", userId));
        }
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(userId);
        return requests.stream()
                .map(request -> {
                    List<Item> items = itemRepository.findByRequestId(request.getId());
                    return ItemRequestMapper.toItemRequestDto(request, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с ID %d не найден", userId));
        }
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException(String.format("Запрос с ID %d не найден", requestId)));
        List<Item> items = itemRepository.findByRequestId(requestId);
        return ItemRequestMapper.toItemRequestDto(itemRequest, items);
    }
}