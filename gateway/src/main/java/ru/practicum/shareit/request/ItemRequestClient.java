package ru.practicum.shareit.request;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.client.BaseClient;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> createRequest(Long userId, ItemRequestDto requestDto) {
        log.info("Creating request for userId: {}, requestDto: {}", userId, requestDto);
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getUserRequests(Long userId) {
        log.info("Getting user requests for userId: {}", userId);
        return get("", userId);
    }

    public ResponseEntity<Object> getAllRequests(Long userId) {
        log.info("Getting all requests for userId: {}", userId);
        return get("/all", userId);
    }

    public ResponseEntity<Object> getRequestById(Long userId, Long requestId) {
        log.info("Getting request by id for userId: {}, requestId: {}", userId, requestId);
        return get("/" + requestId, userId);
    }
}