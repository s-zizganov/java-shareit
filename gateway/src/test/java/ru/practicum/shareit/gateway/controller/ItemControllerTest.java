package ru.practicum.shareit.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient itemClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getAllItemsShouldReturnOk() throws Exception {
        when(itemClient.getAllItems(eq(1L))).thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void createItemShouldReturnOk() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Item", "Desc", true, 0,
                1L, null, null, null, null);
        when(itemClient.createItem(eq(1L), any(ItemDto.class))).thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateItemShouldReturnOk() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "New Item", "New Desc", false, 0,
                1L, null, null, null, null);
        when(itemClient.updateItem(eq(1L), eq(1L), any(ItemDto.class))).thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());
    }

    @Test
    void getItemShouldReturnOk() throws Exception {
        when(itemClient.getItem(eq(1L), eq(1L))).thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getUserItemsShouldReturnOk() throws Exception {
        when(itemClient.getUserItems(eq(1L))).thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/items/owner")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void searchItemsShouldReturnOk() throws Exception {
        when(itemClient.searchItems(eq(1L), any(String.class))).thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", "1")
                        .param("text", "test"))
                .andExpect(status().isOk());
    }

    @Test
    void createCommentShouldReturnOk() throws Exception {
        CommentDto commentDto = new CommentDto(null, "Good item", null);
        when(itemClient.createComment(eq(1L), eq(1L), any(CommentDto.class))).thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk());
    }
}