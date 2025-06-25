package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для работы с сущностями бронирований.
 * Предоставляет методы для выполнения CRUD-операций с объектами Booking.
 * Расширяет JpaRepository, что автоматически обеспечивает базовые методы
 * для работы с данными, такие как findAll(), findById(), save(), delete() и др.
 *
 * @see Booking
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {
}