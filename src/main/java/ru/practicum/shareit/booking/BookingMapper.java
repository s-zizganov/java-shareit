package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

// Класс для преобразования объектов Booking в BookingDto и обратно
public class BookingMapper {
    // Метод преобразует сущность Booking в DTO для передачи через REST API
    public static BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItemId(booking.getItemId());
        bookingDto.setBookerId(booking.getBookerId());
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    // Метод преобразует DTO обратно в сущность Booking
    public static Booking toBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItemId(bookingDto.getItemId());
        booking.setBookerId(bookingDto.getBookerId());
        booking.setStatus(bookingDto.getStatus());
        return booking;
    }
}