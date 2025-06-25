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
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository; // Репозиторий для работы с сущностями бронирований
    private final UserRepository userRepository; // Репозиторий для проверки существования пользователей
    private final ItemRepository itemRepository; // Репозиторий для проверки существования вещей и владельцев

    @Override // Метод создаёт новое бронирование для указанного пользователя и вещи.
    public BookingResponseDto createBooking(Long userId, BookingRequestDto bookingDto) {
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null ||
                bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new IllegalArgumentException("Некорректные даты бронирования");
        }
        if (bookingDto.getItemId() == null) {
            throw new ItemNotFoundException("Идентификатор вещи не указан");
        }
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователь с ID " + userId + " не найден");
        }

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не найден"));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Вещь с ID " + bookingDto.getItemId() + " не найдена"));

        if (!item.getAvailable()) {
            throw new NotAvailableException("Вещь недоступна для бронирования");
        }
        if (item.getOwnerId().equals(userId)) {
            throw new OwnerCannotBookException("Владелец не может бронировать свою вещь");
        }

        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBookerId(userId);
        booking.setStatus(BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingResponseDto(savedBooking, item, booker);
    }

    @Override
    public BookingResponseDto approveBooking(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с ID " + bookingId + " не найдено"));
        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Вещь с ID " + booking.getItemId() + " не найдена"));
        if (!item.getOwnerId().equals(userId)) {
            throw new AccessDeniedException("Только владелец может одобрить бронирование");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new InvalidBookingStateException("Статус бронирования должен быть WAITING");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking savedBooking = bookingRepository.save(booking);
        User booker = userRepository.findById(booking.getBookerId())
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + booking.getBookerId() + " не найден"));
        return BookingMapper.toBookingResponseDto(savedBooking, item, booker);
    }

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
                            .orElseThrow(() -> new ItemNotFoundException("Вещь с ID " + booking.getItemId() + " не найдена"));
                    User booker = userRepository.findById(booking.getBookerId())
                            .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + booking.getBookerId() + " не найден"));
                    return BookingMapper.toBookingResponseDto(booking, item, booker);
                });
    }

    @Override
    public List<BookingResponseDto> getBookings(Long userId, String state) {
        LocalDateTime now = LocalDateTime.now();
        return bookingRepository.findAll().stream()
                .filter(booking -> booking.getBookerId().equals(userId))
                .filter(booking -> filterByState(booking, state, now))
                .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                .map(booking -> {
                    Item item = itemRepository.findById(booking.getItemId())
                            .orElseThrow(() -> new ItemNotFoundException("Вещь с ID " + booking.getItemId() + " не найдена"));
                    User booker = userRepository.findById(booking.getBookerId())
                            .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + booking.getBookerId() + " не найден"));
                    return BookingMapper.toBookingResponseDto(booking, item, booker);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getOwnerBookings(Long userId, String state) {
        // Проверка существования пользователя
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не найден"));

        LocalDateTime now = LocalDateTime.now();
        List<BookingResponseDto> bookings = bookingRepository.findAll().stream()
                .filter(booking -> itemRepository.findById(booking.getItemId())
                        .map(item -> item.getOwnerId().equals(userId)).orElse(false))
                .filter(booking -> filterByState(booking, state, now))
                .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                .map(booking -> {
                    Item item = itemRepository.findById(booking.getItemId())
                            .orElseThrow(() -> new ItemNotFoundException("Вещь с ID " + booking.getItemId() + " не найдена"));
                    User booker = userRepository.findById(booking.getBookerId())
                            .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + booking.getBookerId() + " не найден"));
                    return BookingMapper.toBookingResponseDto(booking, item, booker);
                })
                .collect(Collectors.toList());

        // Если бронирований нет, выбросить исключение
        if (bookings.isEmpty()) {
            throw new NotFoundException("Бронирования для владельца с ID " + userId + " не найдены");
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
                            .orElseThrow(() -> new ItemNotFoundException("Вещь с ID " + booking.getItemId() + " не найдена"));
                    User booker = userRepository.findById(booking.getBookerId())
                            .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + booking.getBookerId() + " не найден"));
                    return BookingMapper.toBookingResponseDto(booking, item, booker);
                })
                .collect(Collectors.toList());
    }

    // Метод фильтрует бронирование по состоянию на основе текущего времени.
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



