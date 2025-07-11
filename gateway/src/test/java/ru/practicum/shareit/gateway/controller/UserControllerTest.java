package ru.practicum.shareit.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createUserShouldReturnOk() throws Exception {
        UserDto userDto = new UserDto(null, "John", "john@example.com");
        when(userClient.createUser(any(UserDto.class))).thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateUserShouldReturnOk() throws Exception {
        UserDto userDto = new UserDto(1L, "Jane", "jane@example.com");
        when(userClient.updateUser(eq(1L), any(UserDto.class))).thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());
    }

    @Test
    void getUserShouldReturnOk() throws Exception {
        when(userClient.getUser(eq(1L))).thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllUsersShouldReturnOk() throws Exception {
        when(userClient.getAllUsers()).thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUserShouldReturnOk() throws Exception {
        when(userClient.deleteUser(eq(1L))).thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}