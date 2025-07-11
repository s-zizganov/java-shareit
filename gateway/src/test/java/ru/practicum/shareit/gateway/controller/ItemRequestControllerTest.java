package ru.practicum.shareit.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestClient itemRequestClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createRequestShouldReturnOk() throws Exception {
        ItemRequestDto dto = new ItemRequestDto(null, "Need item", null);
        when(itemRequestClient.createRequest(eq(1L), any(ItemRequestDto.class))).thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void getUserRequestsShouldReturnOk() throws Exception {
        when(itemRequestClient.getUserRequests(eq(1L))).thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllRequestsShouldReturnOk() throws Exception {
        when(itemRequestClient.getAllRequests(eq(1L))).thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getRequestByIdShouldReturnOk() throws Exception {
        when(itemRequestClient.getRequestById(eq(1L), eq(1L))).thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }
}