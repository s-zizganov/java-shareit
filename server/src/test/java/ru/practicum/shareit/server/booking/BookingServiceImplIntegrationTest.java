package ru.practicum.shareit.server.booking;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItServer.class)
@ActiveProfiles("test")
@Transactional
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void createBookingShouldSaveBooking() {
        // Arrange
        User owner = new User(null, "Owner", "owner@example.com");
        User booker = new User(null, "John", "john@example.com");
        List<User> savedUsers = userRepository.saveAll(List.of(owner, booker));
        User savedOwner = savedUsers.get(0);
        User savedBooker = savedUsers.get(1);
        Item item = new Item(null, "Test Item", "Desc", true, savedOwner.getId(), null);
        Item savedItem = itemRepository.save(item);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequestDto dto = new BookingRequestDto(start, end, savedItem.getId());

        // Act
        BookingResponseDto result = bookingService.createBooking(savedBooker.getId(), dto);

        // Assert
        assertNotNull(result.getId());
        assertEquals(savedItem.getId(), result.getItem().getId());
        assertEquals("WAITING", result.getStatus());
        assertEquals(start, result.getStart());
        assertEquals(end, result.getEnd());
        assertEquals(savedBooker.getId(), result.getBooker().getId());
    }

    @Test
    void createBookingWithInvalidDatesShouldThrowException() {
        // Arrange
        User user = new User(null, "John", "john@example.com");
        User savedUser = userRepository.save(user);
        Item item = new Item(null, "Test Item", "Desc", true, savedUser.getId() + 1, null);
        Item savedItem = itemRepository.save(item); // Сохраняем вещь
        BookingRequestDto dto = new BookingRequestDto(LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(1), savedItem.getId());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(savedUser.getId(), dto));
    }

    @Test
    void createBookingWithUnavailableItemShouldThrowException() {
        // Arrange
        User owner = new User(null, "Owner", "owner@example.com");
        User booker = new User(null, "John", "john@example.com");
        List<User> savedUsers = userRepository.saveAll(List.of(owner, booker));
        User savedOwner = savedUsers.get(0);
        User savedBooker = savedUsers.get(1);
        Item item = new Item(null, "Test Item", "Desc", false, savedOwner.getId(), null);
        Item savedItem = itemRepository.save(item);
        BookingRequestDto dto = new BookingRequestDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), savedItem.getId());

        // Act & Assert
        assertThrows(NotAvailableException.class, () -> bookingService.createBooking(savedBooker.getId(), dto));
    }

    @Test
    void approveBookingShouldUpdateStatus() {
        // Arrange
        User owner = new User(null, "Owner", "owner@example.com");
        User booker = new User(null, "John", "john@example.com");
        List<User> savedUsers = userRepository.saveAll(List.of(owner, booker));
        User savedOwner = savedUsers.get(0);
        User savedBooker = savedUsers.get(1);
        Item item = new Item(null, "Test Item", "Desc", true, savedOwner.getId(), null);
        Item savedItem = itemRepository.save(item);
        BookingRequestDto dto = new BookingRequestDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), savedItem.getId());
        BookingResponseDto booking = bookingService.createBooking(savedBooker.getId(), dto);

        // Act
        BookingResponseDto approved = bookingService.approveBooking(savedOwner.getId(), booking.getId(), true);

        // Assert
        assertEquals("APPROVED", approved.getStatus());
    }

    @Test
    void approveBookingWithWrongUserShouldThrowException() {
        // Arrange
        User owner = new User(null, "Owner", "owner@example.com");
        User booker = new User(null, "John", "john@example.com");
        List<User> savedUsers = userRepository.saveAll(List.of(owner, booker));
        User savedOwner = savedUsers.get(0);
        User savedBooker = savedUsers.get(1);
        Item item = new Item(null, "Test Item", "Desc", true, savedOwner.getId(), null);
        Item savedItem = itemRepository.save(item);
        BookingRequestDto dto = new BookingRequestDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), savedItem.getId());
        BookingResponseDto booking = bookingService.createBooking(savedBooker.getId(), dto);

        // Act & Assert
        assertThrows(ru.practicum.shareit.exception.AccessDeniedException.class, () ->
                bookingService.approveBooking(savedOwner.getId() + 1, booking.getId(), true));
    }

    @Test
    void getBookingShouldReturnBooking() {
        // Arrange
        User owner = new User(null, "Owner", "owner@example.com");
        User booker = new User(null, "John", "john@example.com");
        List<User> savedUsers = userRepository.saveAll(List.of(owner, booker));
        User savedOwner = savedUsers.get(0);
        User savedBooker = savedUsers.get(1);
        Item item = new Item(null, "Test Item", "Desc", true, savedOwner.getId(), null);
        Item savedItem = itemRepository.save(item);
        BookingRequestDto dto = new BookingRequestDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), savedItem.getId());
        BookingResponseDto booking = bookingService.createBooking(savedBooker.getId(), dto);

        // Act
        BookingResponseDto result = bookingService.getBooking(savedBooker.getId(), booking.getId()).get();

        // Assert
        assertEquals(savedBooker.getId(), result.getBooker().getId());
        assertEquals(savedItem.getId(), result.getItem().getId());
    }

    @Test
    void getBookingWithUnauthorizedUserShouldReturnEmpty() {
        // Arrange
        User owner = new User(2L, "Owner", "owner@example.com");
        User booker = new User(1L, "John", "john@example.com");
        Item item = new Item(1L, "Test Item", "Desc", true, 2L, null);
        userRepository.saveAll(List.of(owner, booker));
        itemRepository.save(item);
        BookingRequestDto dto = new BookingRequestDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), 1L);
        BookingResponseDto booking = bookingService.createBooking(1L, dto);

        // Act & Assert
        assertTrue(bookingService.getBooking(3L, booking.getId()).isEmpty());
    }

    @Test
    void getBookingsShouldReturnBookings() {
        // Arrange
        User owner = new User(null, "Owner", "owner@example.com");
        User booker = new User(null, "John", "john@example.com");
        List<User> savedUsers = userRepository.saveAll(List.of(owner, booker));
        User savedOwner = savedUsers.get(0);
        User savedBooker = savedUsers.get(1);
        Item item = new Item(null, "Test Item", "Desc", true, savedOwner.getId(), null);
        Item savedItem = itemRepository.save(item);
        BookingRequestDto dto = new BookingRequestDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), savedItem.getId());
        bookingService.createBooking(savedBooker.getId(), dto);

        // Act
        List<BookingResponseDto> result = bookingService.getBookings(savedBooker.getId(), "ALL");

        // Assert
        assertEquals(1, result.size());
        assertEquals("WAITING", result.get(0).getStatus());
    }

    @Test
    void getOwnerBookingsShouldReturnOwnerBookings() {
        // Arrange
        User owner = new User(null, "Owner", "owner@example.com");
        User booker = new User(null, "John", "john@example.com");
        List<User> savedUsers = userRepository.saveAll(List.of(owner, booker));
        User savedOwner = savedUsers.get(0); // owner получает id=1
        User savedBooker = savedUsers.get(1); // booker получает id=2
        Item item = new Item(null, "Test Item", "Desc", true, savedOwner.getId(), null);
        Item savedItem = itemRepository.save(item);
        BookingRequestDto dto = new BookingRequestDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), savedItem.getId());
        bookingService.createBooking(savedBooker.getId(), dto);

        // Act
        List<BookingResponseDto> result = bookingService.getOwnerBookings(savedOwner.getId(), "ALL");

        // Assert
        assertEquals(1, result.size());
        assertEquals("WAITING", result.get(0).getStatus());
    }

    @Test
    void getBookingsForItemShouldReturnItemBookings() {
        // Arrange
        User owner = new User(null, "Owner", "owner@example.com");
        User booker = new User(null, "John", "john@example.com");
        List<User> savedUsers = userRepository.saveAll(List.of(owner, booker));
        User savedOwner = savedUsers.get(0);
        User savedBooker = savedUsers.get(1);
        Item item = new Item(null, "Test Item", "Desc", true, savedOwner.getId(), null);
        Item savedItem = itemRepository.save(item);
        BookingRequestDto dto = new BookingRequestDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), savedItem.getId());
        bookingService.createBooking(savedBooker.getId(), dto);

        // Act
        List<BookingResponseDto> result = bookingService.getBookingsForItem(savedItem.getId());

        // Assert
        assertEquals(1, result.size());
        assertEquals("WAITING", result.get(0).getStatus());
    }
}