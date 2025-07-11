package ru.practicum.shareit.gateway.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import static org.junit.jupiter.api.Assertions.assertThrows;

class BookItemRequestDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    private final Validator validator;

    public BookItemRequestDtoTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void deserializeBookItemRequestDtoWithPastStartShouldThrowException() throws JsonProcessingException {
        String json = "{\"itemId\": 1, \"start\": \"2025-07-08T15:13:00\", \"end\": \"2025-07-09T16:13:00\"}";
        BookItemRequestDto dto = objectMapper.readValue(json, BookItemRequestDto.class);
        assertThrows(ConstraintViolationException.class, () -> {
            var violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
        });
    }

    @Test
    void deserializeBookItemRequestDtoWithPastEndShouldThrowException() throws JsonProcessingException {
        String json = "{\"itemId\": 1, \"start\": \"2025-07-10T15:13:00\", \"end\": \"2025-07-08T16:13:00\"}";
        BookItemRequestDto dto = objectMapper.readValue(json, BookItemRequestDto.class);
        assertThrows(ConstraintViolationException.class, () -> {
            var violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
        });
    }
}