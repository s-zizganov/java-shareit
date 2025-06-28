package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Класс BookingServiceImpl реализует интерфейс BookingService и предоставляет бизнес-логику
 * для управления бронированиями (создание, утверждение, получение списка и т.д.).
 * Использует Spring Data JPA для взаимодействия с базой данных.
 */
@Service // Аннотация указывает Spring, что этот класс является сервисом
@RequiredArgsConstructor // Lombok аннотация для создания конструктора с обязательными полями
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository; // Репозиторий для работы с сущностями бронирований
    private final UserRepository userRepository; // Репозиторий для проверки существования пользователей
    private final ItemRepository itemRepository; // Репозиторий для проверки существования вещей и владельцев

    /**
     * Создает новое бронирование для указанного пользователя и вещи.
     * Проверяет корректность дат, доступность вещи и права пользователя.
     * @param userId     ID пользователя, создающего бронирование
     * @param bookingDto DTO с данными для создания бронирования
     * @return DTO с информацией о созданном бронировании
     * @throws IllegalArgumentException  если даты бронирования некорректны
     * @throws ItemNotFoundException     если вещь не найдена
     * @throws UserNotFoundException     если пользователь не найден
     * @throws NotAvailableException     если вещь недоступна для бронирования
     * @throws OwnerCannotBookException  если пользователь пытается забронировать свою вещь
     */
    @Override
    public BookingResponseDto createBooking(Long userId, BookingRequestDto bookingDto) {
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null ||
                bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new IllegalArgumentException(String.format("Некорректные даты бронирования для вещи с ID %d",
                    bookingDto.getItemId() != null ? bookingDto.getItemId() : "не указан"));
        }
        if (bookingDto.getItemId() == null) {
            throw new ItemNotFoundException("Идентификатор вещи не указан в запросе на бронирование");
        }
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с ID %d не найден при создании бронирования",
                    userId));
        }

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с ID %d не найден при создании " +
                        "бронирования", userId)));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с ID %d не найдена при создании " +
                        "бронирования", bookingDto.getItemId())));

        if (!item.getAvailable()) {
            throw new NotAvailableException(String.format("Вещь с ID %d недоступна для бронирования",
                    bookingDto.getItemId()));
        }
        if (item.getOwnerId().equals(userId)) {
            throw new OwnerCannotBookException(String.format("Пользователь с ID %d не может бронировать свою вещь " +
                    "с ID %d", userId, bookingDto.getItemId()));
        }

        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBookerId(userId);
        booking.setStatus(BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingResponseDto(savedBooking, item, booker);
    }

    /**
     * Подтверждает или отклоняет запрос на бронирование.
     * Только владелец вещи может изменить статус бронирования.
     * @param userId    ID пользователя, подтверждающего бронирование (владелец вещи)
     * @param bookingId ID бронирования, которое нужно подтвердить или отклонить
     * @param approved  true для подтверждения, false для отклонения
     * @return DTO с информацией об обновленном бронировании
     * @throws BookingNotFoundException    если бронирование не найдено
     * @throws ItemNotFoundException      если вещь не найдена
     * @throws AccessDeniedException      если пользователь не является владельцем вещи
     * @throws InvalidBookingStateException если статус бронирования не WAITING
     */
    @Override
    public BookingResponseDto approveBooking(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(String.format("Бронирование с ID %d не найдено",
                        bookingId)));
        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с ID %d не найдена для бронирования %d"
                        , booking.getItemId(), bookingId)));
        if (!item.getOwnerId().equals(userId)) {
            throw new AccessDeniedException(String.format("Пользователь с ID %d не является владельцем вещи с ID %d",
                    userId, booking.getItemId()));
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new InvalidBookingStateException(String.format("Статус бронирования с ID %d должен быть WAITING, " +
                    "текущий статус: %s", bookingId, booking.getStatus()));
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking savedBooking = bookingRepository.save(booking);
        User booker = userRepository.findById(booking.getBookerId())
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с ID %d не найден " +
                        "для бронирования %d", booking.getBookerId(), bookingId)));
        return BookingMapper.toBookingResponseDto(savedBooking, item, booker);
    }

    /**
     * Получает информацию о конкретном бронировании.
     * Доступно только автору бронирования или владельцу вещи.
     * @param userId    ID пользователя, запрашивающего информацию
     * @param bookingId ID запрашиваемого бронирования
     * @return Optional с DTO бронирования или пустой Optional, если бронирование не найдено
     *         или пользователь не имеет прав на просмотр
     */
    @Override
    public Optional<BookingResponseDto> getBooking(Long userId, Long bookingId) {
        return bookingRepository.findById(bookingId)
                .filter(booking -> {
                    Item item = itemRepository.findById(booking.getItemId()).orElse(null);
                    return booking.getBookerId().equals(userId) ||
                            (item != null && item.getOwnerId().equals(userId));
                })
                .map(booking -> {
                    Item item = itemRepository.findById(booking.getItemId())
                            .orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с ID %d не найдена для " +
                                    "бронирования %d", booking.getItemId(), bookingId)));
                    User booker = userRepository.findById(booking.getBookerId())
                            .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с ID %d не " +
                                    "найден для бронирования %d", booking.getBookerId(), bookingId)));
                    return BookingMapper.toBookingResponseDto(booking, item, booker);
                });
    }

    /**
     * Получает список всех бронирований пользователя с фильтрацией по состоянию.
     * Возможные значения state: ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED.
     * @param userId ID пользователя, чьи бронирования нужно получить
     * @param state  Состояние бронирований для фильтрации
     * @return Список DTO бронирований, отсортированный по дате начала (от новых к старым)
     */
    @Override
    public List<BookingResponseDto> getBookings(Long userId, String state) {
        LocalDateTime now = LocalDateTime.now();
        return bookingRepository.findAll().stream()
                .filter(booking -> booking.getBookerId().equals(userId))
                .filter(booking -> filterByState(booking, state, now))
                .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart())) // Сортировка от новых к старым
                .map(booking -> {
                    Item item = itemRepository.findById(booking.getItemId())
                            .orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с ID %d не найдена для" +
                                    " бронирования %d", booking.getItemId(), booking.getId())));
                    User booker = userRepository.findById(booking.getBookerId())
                            .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с ID %d не найден" +
                                    " для бронирования %d", booking.getBookerId(), booking.getId())));
                    return BookingMapper.toBookingResponseDto(booking, item, booker);
                })
                .collect(Collectors.toList());
    }

    /**
     * Получает список всех бронирований для вещей, принадлежащих указанному пользователю.
     * Возможные значения state: ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED.
     * @param userId ID пользователя-владельца вещей
     * @param state  Состояние бронирований для фильтрации
     * @return Список DTO бронирований, отсортированный по дате начала (от новых к старым)
     * @throws UserNotFoundException если пользователь не найден
     * @throws NotFoundException если бронирования для владельца не найдены
     */
    @Override
    public List<BookingResponseDto> getOwnerBookings(Long userId, String state) {
        // Проверка существования пользователя
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с ID %d не найден при " +
                        "запросе бронирований владельца", userId)));

        LocalDateTime now = LocalDateTime.now();
        List<BookingResponseDto> bookings = bookingRepository.findAll().stream()
                .filter(booking -> itemRepository.findById(booking.getItemId())
                        .map(item -> item.getOwnerId().equals(userId)).orElse(false))
                .filter(booking -> filterByState(booking, state, now))
                .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart())) // Сортировка от новых к старым
                .map(booking -> {
                    Item item = itemRepository.findById(booking.getItemId())
                            .orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с ID %d не найдена " +
                                    "для бронирования %d", booking.getItemId(), booking.getId())));
                    User booker = userRepository.findById(booking.getBookerId())
                            .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с ID %d не " +
                                    "найден для бронирования %d", booking.getBookerId(), booking.getId())));
                    return BookingMapper.toBookingResponseDto(booking, item, booker);
                })
                .collect(Collectors.toList());

        // Если бронирований нет, выбросить исключение
        if (bookings.isEmpty()) {
            throw new NotFoundException(String.format("Бронирования для владельца с ID %d не найдены для состояния %s",
                    userId, state));
        }

        return bookings;
    }

    /**
     * Получает список всех бронирований для указанной вещи.
     * @param itemId ID вещи
     * @return List<BookingResponseDto> список бронирований
     */
    @Override
    public List<BookingResponseDto> getBookingsForItem(Long itemId) {
        return bookingRepository.findAll().stream()
                .filter(booking -> booking.getItemId().equals(itemId))
                .map(booking -> {
                    Item item = itemRepository.findById(booking.getItemId())
                            .orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с ID %d не " +
                                    "найдена для бронирования %d", booking.getItemId(), booking.getId())));
                    User booker = userRepository.findById(booking.getBookerId())
                            .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с ID %d не" +
                                    " найден для бронирования %d", booking.getBookerId(), booking.getId())));
                    return BookingMapper.toBookingResponseDto(booking, item, booker);
                })
                .collect(Collectors.toList());
    }

    /**
     * Фильтрует бронирование по состоянию на основе текущего времени и статуса.
     * @param booking Бронирование для проверки
     * @param state Строковое представление состояния для фильтрации (CURRENT, PAST, FUTURE, WAITING, REJECTED, ALL)
     * @param now Текущее время для сравнения с датами бронирования
     * @return true если бронирование соответствует указанному состоянию, иначе false
     */
    private boolean filterByState(Booking booking, String state, LocalDateTime now) {
        return switch (state.toUpperCase()) {
            case "CURRENT" -> booking.getStart().isBefore(now) && booking.getEnd().isAfter(now);
            case "PAST" -> booking.getEnd().isBefore(now);
            case "FUTURE" -> booking.getStart().isAfter(now);
            case "WAITING" -> booking.getStatus() == BookingStatus.WAITING;
            case "REJECTED" -> booking.getStatus() == BookingStatus.REJECTED;
            case "ALL" -> true;
            default -> false;
        };
    }

}



