package ru.practicum.shareit.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()); // Регистрация JavaTimeModule

    @Test
    public void createBookingShouldReturnOk() throws Exception {
        BookingRequestDto dto = new BookingRequestDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), 1L);
        when(bookingClient.createBooking(eq(1L), any(BookingRequestDto.class))).thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    public void approveBookingShouldReturnOk() throws Exception {
        when(bookingClient.approveBooking(eq(1L), eq(1L), eq(true))).thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true"))
                .andExpect(status().isOk());
    }

    @Test
    public void getBookingShouldReturnOk() throws Exception {
        when(bookingClient.getBooking(eq(1L), eq(1L))).thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    public void getBookingsShouldReturnOk() throws Exception {
        when(bookingClient.getBookings(eq(1L), any(String.class))).thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL"))
                .andExpect(status().isOk());
    }

    @Test
    public void getOwnerBookingsShouldReturnOk() throws Exception {
        when(bookingClient.getOwnerBookings(eq(1L), any(String.class))).thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL"))
                .andExpect(status().isOk());
    }
}