package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.client.BaseClient;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> createUser(UserDto userDto) {
        log.info("Creating user: {}", userDto);
        return post("", null, userDto);
    }

    public ResponseEntity<Object> updateUser(Long userId, UserDto userDto) {
        log.info("Updating user ID {}: {}", userId, userDto);
        return patch("/" + userId, userId, userDto);
    }

    public ResponseEntity<Object> getUser(Long userId) {
        log.info("Getting user ID: {}", userId);
        return get("/" + userId, userId);
    }

    public ResponseEntity<Object> getAllUsers() {
        log.info("Getting all users");
        return get("", null);
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        log.info("Deleting user ID: {}", userId);
        return delete("/" + userId, userId);
    }
}