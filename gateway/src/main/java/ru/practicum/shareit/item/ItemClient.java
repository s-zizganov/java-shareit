package ru.practicum.shareit.item;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.client.BaseClient;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> createItem(Long userId, ItemDto itemDto) {
        log.info("Creating item for userId: {}, itemDto: {}", userId, itemDto);
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemDto itemDto) {
        log.info("Updating item for userId: {}, itemId: {}, itemDto: {}", userId, itemId, itemDto);
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getItem(Long userId, Long itemId) {
        log.info("Getting item for userId: {}, itemId: {}", userId, itemId);
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllItems(Long userId) {
        log.info("Getting all items for userId: {}", userId);
        return get("", userId);
    }

    public ResponseEntity<Object> getUserItems(Long userId) {
        log.info("Getting user items for userId: {}", userId);
        return get("/owner", userId);
    }

    public ResponseEntity<Object> searchItems(Long userId, String text) {
        log.info("Searching items for userId: {}, text: {}", userId, text);
        return get("/search?text={text}", userId, Map.of("text", text));
    }

    public ResponseEntity<Object> createComment(Long userId, Long itemId, CommentDto commentDto) {
        log.info("Creating comment for userId: {}, itemId: {}, commentDto: {}", userId, itemId, commentDto);
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}