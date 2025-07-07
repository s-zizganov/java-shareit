package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

/**
 * Класс для преобразования объектов Booking в DTO и обратно.
 * Содержит статические методы для конвертации между сущностями и DTO.
 */
public class BookingMapper {
    /**
     * Преобразует сущность Booking в DTO для ответа.
     * @param booking сущность бронирования, которую нужно преобразовать
     * @param item вещь, связанная с бронированием
     * @param booker пользователь, создавший бронирование
     * @return объект BookingResponseDto с данными из сущности
     */
    public static BookingResponseDto toBookingResponseDto(Booking booking, Item item, User booker) {
        BookingResponseDto dto = new BookingResponseDto();
        // Устанавливаем идентификатор бронирования
        dto.setId(booking.getId());
        // Устанавливаем время начала бронирования
        dto.setStart(booking.getStart());
        // Устанавливаем время окончания бронирования
        dto.setEnd(booking.getEnd());
        // Преобразуем Item в ItemDto и устанавливаем в DTO
        dto.setItem(ItemMapper.toItemDto(item)); // Преобразуем Item в ItemDto
        // Преобразуем User в UserDto и устанавливаем в DTO
        dto.setBooker(UserMapper.toUserDto(booker)); // Преобразуем User в UserDto
        // Устанавливаем статус бронирования, проверяя на null
        dto.setStatus(booking.getStatus() != null ? booking.getStatus().name() : null);
        return dto;
    }

    /**
     * Преобразует DTO запроса в сущность Booking.
     * @param bookingDto DTO с данными для создания бронирования
     * @return новый объект Booking с данными из DTO
     */
    public static Booking toBooking(BookingRequestDto bookingDto) {
        Booking booking = new Booking();
        // Устанавливаем время начала бронирования из DTO
        booking.setStart(bookingDto.getStart());
        // Устанавливаем время окончания бронирования из DTO
        booking.setEnd(bookingDto.getEnd());
        // Устанавливаем идентификатор вещи из DTO
        booking.setItemId(bookingDto.getItemId());
        return booking;
    }
}