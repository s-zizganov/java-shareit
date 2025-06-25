package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingRequestDto {
    @NotNull(message = "Дата начала не может быть пустой")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания не может быть пустой")
    private LocalDateTime end;

    @NotNull(message = "Идентификатор вещи не может быть пустым")
    @Positive(message = "Идентификатор вещи должен быть положительным")
    private Long itemId;
}