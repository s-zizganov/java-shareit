package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с вещами.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingService bookingService;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        log.info("Creating item for user ID {}, itemDto: {}", userId, itemDto);
        if (!userService.getUser(userId).isPresent()) {
            throw new UserNotFoundException(String.format("Пользователь с ID %d не найден", userId));
        }
        if (itemDto.getRequestId() != null && !itemRequestRepository.existsById(itemDto.getRequestId())) {
            throw new ItemRequestNotFoundException(String.format("Запрос с ID %d не найден", itemDto.getRequestId()));
        }
        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(userId);
        Item savedItem = itemRepository.save(item);
        log.info("Saved item: {}", savedItem);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        if (!userService.getUser(userId).isPresent()) {
            throw new UserNotFoundException(String.format("Пользователь с ID %d не найден", userId));
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Вещь с ID %d не найдена", itemId)));
        if (!item.getOwnerId().equals(userId)) {
            throw new IllegalArgumentException("Пользователь не является владельцем");
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        Item updatedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        if (!userService.getUser(userId).isPresent()) {
            throw new UserNotFoundException(String.format("Пользователь с ID %d не найден", userId));
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Вещь с ID %d не найдена", itemId)));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        fillBookingDates(itemDto, item.getId(), userId);
        fillComments(itemDto, itemId);
        return itemDto;
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        if (!userService.getUser(userId).isPresent()) {
            throw new UserNotFoundException(String.format("Пользователь с ID %d не найден", userId));
        }
        List<Item> items = itemRepository.findByOwnerId(userId);
        return items.stream()
                .map(item -> {
                    ItemDto dto = ItemMapper.toItemDto(item);
                    fillBookingDates(dto, item.getId(), userId);
                    fillComments(dto, item.getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        if (!userService.getUser(userId).isPresent()) {
            throw new UserNotFoundException(String.format("Пользователь с ID %d не найден", userId));
        }
        List<Item> items = itemRepository.findByOwnerId(userId);
        return items.stream()
                .map(item -> {
                    ItemDto dto = ItemMapper.toItemDto(item);
                    fillBookingDates(dto, item.getId(), userId);
                    fillComments(dto, item.getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(Long userId, String text) {
        if (!userService.getUser(userId).isPresent()) {
            throw new UserNotFoundException(String.format("Пользователь с ID %d не найден", userId));
        }
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.findAll().stream()
                .filter(item -> item.getAvailable() != null && item.getAvailable() &&
                        (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        if (!userService.getUser(userId).isPresent()) {
            throw new UserNotFoundException(String.format("Пользователь с ID %d не найден", userId));
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Вещь с ID %d не найдена", itemId)));
        List<BookingResponseDto> bookings = bookingService.getBookingsForItem(itemId);
        boolean hasBooked = bookings.stream()
                .anyMatch(booking -> booking.getBooker().getId().equals(userId) &&
                        booking.getEnd().isBefore(LocalDateTime.now()) &&
                        booking.getStatus().equals("APPROVED"));
        if (!hasBooked) {
            throw new IllegalArgumentException("Пользователь не арендовал эту вещь");
        }
        String authorName = userService.getUser(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"))
                .getName();
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItemId(itemId);
        comment.setAuthorId(userId);
        comment.setCreated(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(savedComment, authorName);
    }

    private void fillBookingDates(ItemDto itemDto, Long itemId, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Вещь с ID %d не найдена", itemId)));
        if (!item.getOwnerId().equals(userId)) {
            itemDto.setLastBooking(null);
            itemDto.setNextBooking(null);
            return;
        }
        List<BookingResponseDto> bookings = bookingService.getBookingsForItem(itemId);
        if (bookings != null && !bookings.isEmpty()) {
            itemDto.setLastBooking(bookings.stream()
                    .filter(b -> b.getEnd().isBefore(now) && b.getStatus().equals("APPROVED"))
                    .max((b1, b2) -> b1.getEnd().compareTo(b2.getEnd()))
                    .map(BookingResponseDto::getEnd)
                    .orElse(null));
            itemDto.setNextBooking(bookings.stream()
                    .filter(b -> b.getStart().isAfter(now) && b.getStatus().equals("APPROVED"))
                    .min((b1, b2) -> b1.getStart().compareTo(b2.getStart()))
                    .map(BookingResponseDto::getStart)
                    .orElse(null));
        }
    }

    private void fillComments(ItemDto itemDto, Long itemId) {
        List<Comment> comments = commentRepository.findByItemId(itemId);
        List<CommentDto> commentDtos = comments.stream()
                .map(comment -> {
                    String authorName = userService.getUser(comment.getAuthorId())
                            .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"))
                            .getName();
                    return CommentMapper.toCommentDto(comment, authorName);
                })
                .collect(Collectors.toList());
        itemDto.setComments(commentDtos);
    }
}