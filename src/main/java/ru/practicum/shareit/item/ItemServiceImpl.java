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
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с вещами.
 */
@Service
@RequiredArgsConstructor // Добавлена аннотация @RequiredArgsConstructor для автоматической инъекции ItemRepository
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    // Добавлена зависимость от BookingService для получения данных о бронированиях
    private final BookingService bookingService;

    private final CommentRepository commentRepository;
    // Изменение: Добавлена зависимость от UserService для получения имени автора
    private final UserService userService;

    private Long idCounter = 1L;

    /**
     * {@inheritDoc}
     */
    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        log.info("Creating item with userId: {}, itemDto: {}", userId, itemDto);
        Item item = ItemMapper.toItem(itemDto);
        item.setId(idCounter++);
        item.setOwnerId(userId);
        Item savedItem = itemRepository.save(item);
        log.info("Saved item: {}", savedItem);
        return ItemMapper.toItemDto(savedItem);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Вещь не найдена"));
        if (!item.getOwnerId().equals(userId)) {
            throw new RuntimeException("Пользователь не является владельцем");
        }
        // Обновляем поля, если они предоставлены
        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Вещь не найдена"));


        ItemDto itemDto = ItemMapper.toItemDto(item);
        fillBookingDates(itemDto, item.getId(), userId);
        fillComments(itemDto, itemId);
        return itemDto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ItemDto> getAllItems(Long userId) {
        List<Item> items = itemRepository.findByOwnerId(userId);
        List<ItemDto> itemDtos = items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        itemDtos.forEach(dto -> {
            fillBookingDates(dto, dto.getId(), userId);
            fillComments(dto, dto.getId());
        });
        return itemDtos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ItemDto> getUserItems(Long userId) {
        List<Item> items = itemRepository.findAll().stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .collect(Collectors.toList());
        List<ItemDto> itemDtos = items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        itemDtos.forEach(dto -> {
            fillBookingDates(dto, dto.getId(), userId);
            fillComments(dto, dto.getId());
        });
        return itemDtos;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<ItemDto> searchItems(Long userId, String text) {
        if (text == null || text.isEmpty()) return List.of();
        return itemRepository.findAll().stream()
                .filter(item -> item.getAvailable() != null && item.getAvailable() &&
                        (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        // Проверка существования вещи
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Вещь не найдена"));
        // Проверка, что пользователь арендовал вещь
        List<BookingResponseDto> bookings = bookingService.getBookingsForItem(itemId);
        boolean hasBooked = bookings.stream()
                .anyMatch(booking -> booking.getBooker().getId().equals(userId) &&
                        booking.getEnd().isBefore(LocalDateTime.now()) &&
                        booking.getStatus().equals("APPROVED"));
        if (!hasBooked) {
            throw new RuntimeException("Пользователь не арендовал эту вещь");
        }
        String authorName = userService.getUser(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"))
                .getName();
        // Создание комментария
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItemId(itemId);
        comment.setAuthorId(userId);
        comment.setCreated(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(savedComment, authorName);
    }

    /**
     * Заполняет даты последнего и ближайшего бронирования для указанной вещи.
     * @param itemDto DTO вещи, для которой нужно заполнить даты
     * @param itemId ID вещи
     * @param userId ID текущего пользователя
     */
    private void fillBookingDates(ItemDto itemDto, Long itemId, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        // Проверяем, является ли пользователь владельцем вещи
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Вещь не найдена"));
        if (!item.getOwnerId().equals(userId)) {
            // Для невладельцев устанавливаем lastBooking и nextBooking в null
            itemDto.setLastBooking(null);
            itemDto.setNextBooking(null);
            return;
        }
        // Для владельца заполняем даты бронирований
        List<BookingResponseDto> bookings = bookingService.getBookingsForItem(itemId);
        if (bookings != null && !bookings.isEmpty()) {
            // Находим последнее бронирование (максимальная end дата до now)
            itemDto.setLastBooking(bookings.stream()
                    .filter(b -> b.getEnd().isBefore(now) && b.getStatus().equals("APPROVED"))
                    .max((b1, b2) -> b1.getEnd().compareTo(b2.getEnd()))
                    .map(BookingResponseDto::getEnd)
                    .orElse(null));
            // Находим ближайшее следующее бронирование (минимальная start дата после now)
            itemDto.setNextBooking(bookings.stream()
                    .filter(b -> b.getStart().isAfter(now) && b.getStatus().equals("APPROVED"))
                    .min((b1, b2) -> b1.getStart().compareTo(b2.getStart()))
                    .map(BookingResponseDto::getStart)
                    .orElse(null));
        }
    }

    /**
     * Заполняет DTO вещи списком комментариев.
     * @param itemDto DTO вещи, для которой нужно заполнить комментарии
     * @param itemId ID вещи
     */
    private void fillComments(ItemDto itemDto, Long itemId) {
        List<Comment> comments = commentRepository.findByItemId(itemId);
        List<CommentDto> commentDtos = comments.stream()
                .map(comment -> {
                    String authorName = userService.getUser(comment.getAuthorId())
                            .orElseThrow(() -> new RuntimeException("Пользователь не найден"))
                            .getName();
                    return CommentMapper.toCommentDto(comment, authorName);
                })
                .collect(Collectors.toList());
        itemDto.setComments(commentDtos);
    }
}