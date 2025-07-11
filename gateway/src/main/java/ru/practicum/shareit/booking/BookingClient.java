package ru.practicum.shareit.booking;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.client.BaseClient;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> createBooking(Long userId, BookingRequestDto bookingDto) {
        log.info("Creating booking for userId: {}, bookingDto: {}", userId, bookingDto);
        return post("", userId, bookingDto);
    }

    public ResponseEntity<Object> approveBooking(Long userId, Long bookingId, boolean approved) {
        log.info("Approving booking for userId: {}, bookingId: {}, approved: {}", userId, bookingId, approved);
        Map<String, Object> parameters = Map.of("approved", approved);
        return patch("/" + bookingId + "?approved={approved}", userId, parameters, null);
    }

    public ResponseEntity<Object> getBooking(Long userId, Long bookingId) {
        log.info("Getting booking for userId: {}, bookingId: {}", userId, bookingId);
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookings(Long userId, String state) {
        log.info("Getting bookings for userId: {}, state: {}", userId, state);
        return get("?state={state}", userId, Map.of("state", state));
    }

    public ResponseEntity<Object> getOwnerBookings(Long userId, String state) {
        log.info("Getting owner bookings for userId: {}, state: {}", userId, state);
        return get("/owner?state={state}", userId, Map.of("state", state));
    }
}