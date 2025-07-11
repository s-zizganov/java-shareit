package ru.practicum.shareit.server.user; // Исправлен пакет

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.ShareItServer; // Импорт основного класса
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItServer.class)
@Transactional
@ActiveProfiles("test")
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void createUserShouldSaveUser() {
        UserDto userDto = new UserDto(null, "John", "john@example.com");
        UserDto created = userService.createUser(userDto);
        assertEquals("John", created.getName());
    }

    @Test
    void updateUserShouldUpdateUser() {
        UserDto userDto = new UserDto(null, "John", "john@example.com");
        UserDto created = userService.createUser(userDto);
        UserDto updateDto = new UserDto(created.getId(), "Jane", "jane@example.com");
        UserDto updated = userService.updateUser(created.getId(), updateDto);
        assertEquals("Jane", updated.getName());
    }

    @Test
    void getUserShouldReturnUser() {
        UserDto userDto = new UserDto(null, "John", "john@example.com");
        UserDto created = userService.createUser(userDto);
        assertEquals("John", userService.getUser(created.getId()).get().getName());
    }

    @Test
    void getAllUsersShouldReturnAllUsers() {
        UserDto user1 = new UserDto(null, "John", "john@example.com");
        UserDto user2 = new UserDto(null, "Jane", "jane@example.com");
        userService.createUser(user1);
        userService.createUser(user2);
        List<UserDto> users = userService.getAllUsers();
        assertEquals(2, users.size());
    }

    @Test
    void deleteUserShouldRemoveUser() {
        UserDto userDto = new UserDto(null, "John", "john@example.com");
        UserDto created = userService.createUser(userDto);
        userService.deleteUser(created.getId());
        assertTrue(userService.getUser(created.getId()).isEmpty());
    }
}