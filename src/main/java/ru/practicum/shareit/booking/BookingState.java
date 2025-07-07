package ru.practicum.shareit.booking;

/**
 * Перечисление для представления допустимых состояний фильтрации бронирований в запросах.
 */
public enum BookingState {
    ALL, // Все бронирования
    CURRENT, // Текущие бронирования (между start и end)
    PAST, // Завершённые бронирования (end < now)
    FUTURE, // Будущие бронирования (start > now)
    WAITING, // Ожидающие подтверждения
    REJECTED // Отклонённые бронирования
}