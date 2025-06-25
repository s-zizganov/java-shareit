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
        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override // Метод утверждает или отклоняет существующее бронирование.
    public BookingResponseDto approveBooking(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с ID " + bookingId + " не найдено"));
        if (!itemRepository.findById(booking.getItemId())
                .map(item -> item.getOwnerId().equals(userId)).orElse(false)) {
            throw new AccessDeniedException("Только владелец может одобрить бронирование");
        }
        if (!"WAITING".equals(booking.getStatus())) {
            throw new InvalidBookingStateException("Статус бронирования должен быть WAITING");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override // Метод получает данные о конкретном бронировании.
    public Optional<BookingResponseDto> getBooking(Long userId, Long bookingId) {
        // Поиск бронирования по ID
        return bookingRepository.findById(bookingId)
                // Фильтрация: доступ только для арендодателя или владельца вещи
                .filter(booking -> booking.getBookerId().equals(userId) ||
                        itemRepository.findById(booking.getItemId())
                                .map(item -> item.getOwnerId().equals(userId)).orElse(false))
                // Преобразование в DTO, если доступ разрешён
                .map(BookingMapper::toBookingResponseDto);
    }

    @Override // Метод получает список бронирований для указанного пользователя.
    public List<BookingResponseDto> getBookings(Long userId, String state) {
        LocalDateTime now = LocalDateTime.now(); // Текущая дата и время для фильтрации
        // Получение всех бронирований и фильтрация по ID арендодателя
        return bookingRepository.findAll().stream()
                .filter(booking -> booking.getBookerId().equals(userId))
                // Фильтрация по состоянию (state)
                .filter(booking -> filterByState(booking, state, now))
                // Сортировка по убыванию даты начала
                .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                // Преобразование в список DTO
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override // Метод получает список бронирований для вещей, принадлежащих указанному пользователю.
    public List<BookingResponseDto> getOwnerBookings(Long userId, String state) {
        LocalDateTime now = LocalDateTime.now(); // Текущая дата и время для фильтрации
        // Получение всех бронирований и фильтрация по владельцу вещи
        return bookingRepository.findAll().stream()
                .filter(booking -> itemRepository.findById(booking.getItemId())
                        .map(item -> item.getOwnerId().equals(userId)).orElse(false))
                // Фильтрация по состоянию (state)
                .filter(booking -> filterByState(booking, state, now))
                // Сортировка по убыванию даты начала
                .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                // Преобразование в список DTO
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Получает список всех бронирований для указанной вещи.
     * @param itemId ID вещи
     * @return List<BookingResponseDto> список бронирований
*/
    @Override
    public List<BookingResponseDto> getBookingsForItem(Long itemId) {
        // Изменение: Фильтрация бронирований по itemId
        return bookingRepository.findAll().stream()
                .filter(booking -> booking.getItemId().equals(itemId))
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    // Метод фильтрует бронирование по состоянию на основе текущего времени.
    private boolean filterByState(Booking booking, String state, LocalDateTime now) {
        // Логика фильтрации в зависимости от значения state
        return switch (state.toUpperCase()) {
            case "CURRENT" -> booking.getStart().isBefore(now) && booking.getEnd().isAfter(now); // Текущие бронирования
            case "PAST" -> booking.getEnd().isBefore(now); // Завершённые бронирования
            case "FUTURE" -> booking.getStart().isAfter(now); // Будущие бронирования
            case "WAITING" -> "WAITING".equals(booking.getStatus()); // Ожидающие подтверждения
            case "REJECTED" -> "REJECTED".equals(booking.getStatus()); // Отклонённые
            case "ALL" -> true; // Все бронирования
            default -> false; // Неверный state
        };
    }

}



