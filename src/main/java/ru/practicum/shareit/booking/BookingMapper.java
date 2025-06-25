package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

/**
 * Класс для преобразования объектов Booking в DTO и обратно.
 */
public class BookingMapper {
    /**
     * Преобразует сущность Booking в DTO для ответа.
     */
    public static BookingResponseDto toBookingResponseDto(Booking booking, Item item, User booker) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setItem(ItemMapper.toItemDto(item)); // Преобразуем Item в ItemDto
        dto.setBooker(UserMapper.toUserDto(booker)); // Преобразуем User в UserDto
        dto.setStatus(booking.getStatus() != null ? booking.getStatus().name() : null);
        return dto;
    }

    /**
     * Преобразует DTO запроса в сущность Booking.
     */
    public static Booking toBooking(BookingRequestDto bookingDto) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItemId(bookingDto.getItemId());
        return booking;
    }
}