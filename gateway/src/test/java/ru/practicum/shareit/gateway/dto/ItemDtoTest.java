package ru.practicum.shareit.gateway.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    @Test
    void serializeItemDtoShouldSucceed() throws JsonProcessingException {
        ItemDto itemDto = new ItemDto(1L, "Test Item", "Test Desc", true, 0,
                1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), List.of(), null);
        String json = objectMapper.writeValueAsString(itemDto);
        assertTrue(json.contains("Test Item"));
        assertTrue(json.contains("Test Desc"));
    }
}