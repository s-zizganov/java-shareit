package ru.practicum.shareit.server.item;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = ShareItServer.class)
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private Long userId;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
        User user = new User(null, "Test User", "test@example.com");
        user = userRepository.save(user);
        System.out.println("Saved user: " + user);
        this.userId = user.getId();
    }

    @Test
    void createItemShouldSaveItem() {
        ItemDto itemDto = new ItemDto(null, "Test Item", "Test Desc", true, null, userId, null, null, null, null);
        ItemDto created = itemService.createItem(userId, itemDto);
        assertEquals("Test Item", created.getName());
    }

    @Test
    void updateItemShouldUpdateItem() {
        Item item = new Item(null, "Old Item", "Old Desc", true, userId, null);
        Item savedItem = itemRepository.save(item);
        ItemDto itemDto = new ItemDto(savedItem.getId(), "New Item", "New Desc", false, null, userId, null, null, null, null);
        ItemDto updated = itemService.updateItem(userId, savedItem.getId(), itemDto);
        assertEquals("New Item", updated.getName());
        assertEquals(false, updated.getAvailable());
    }

    @Test
    void getAllItemsShouldReturnAllItems() {
        Item item1 = new Item(null, "Item 1", "Desc 1", true, userId, null);
        Item item2 = new Item(null, "Item 2", "Desc 2", true, userId, null);
        itemRepository.saveAll(List.of(item1, item2));
        List<ItemDto> result = itemService.getAllItems(userId);
        assertEquals(2, result.size());
    }

    @Test
    void getUserItemsShouldReturnUserItems() {
        Item item1 = new Item(null, "Item 1", "Desc 1", true, userId, null);
        Item item2 = new Item(null, "Item 2", "Desc 2", true, userId, null);
        itemRepository.saveAll(List.of(item1, item2));
        List<ItemDto> result = itemService.getUserItems(userId);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(item -> item.getOwnerId().equals(userId)));
    }

    @Test
    void searchItemsShouldReturnMatchingItems() {
        Item item1 = new Item(null, "Test Item", "Test Desc", true, userId, null);
        itemRepository.save(item1);
        List<ItemDto> result = itemService.searchItems(userId, "test");
        assertEquals(1, result.size());
        assertEquals("Test Item", result.get(0).getName());
    }

    @Test
    void createCommentShouldSaveComment() {
        Item item = new Item(null, "Test Item", "Test Desc", true, userId, null);
        Item savedItem = itemRepository.save(item);
        Booking booking = new Booking();
        booking.setItemId(savedItem.getId());
        booking.setBookerId(userId);
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now());
        booking.setStatus(BookingServiceImpl.BookingStatus.APPROVED);
        bookingRepository.save(booking);
        CommentDto commentDto = new CommentDto(null, "Good item", null);
        CommentDto result = itemService.createComment(userId, savedItem.getId(), commentDto);
        assertEquals("Good item", result.getText());
    }
}