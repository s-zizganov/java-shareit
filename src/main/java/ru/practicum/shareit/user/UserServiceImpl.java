package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    // Простое регулярное выражение для проверки email (пример)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$"
    );

    @Override
    public UserDto createUser(UserDto userDto) {
        log.debug("Validating user DTO: {}", userDto);
        // Валидация входных данных
        if (userDto == null || userDto.getName() == null || userDto.getName().trim().isEmpty() ||
                userDto.getEmail() == null || userDto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Name or email cannot be null or empty");
        }

        // Проверка формата email
        log.debug("Checking email format: {}", userDto.getEmail());
        if (!EMAIL_PATTERN.matcher(userDto.getEmail().trim()).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + userDto.getEmail());
        }

        // Проверка уникальности email
        log.debug("Checking email uniqueness: {}", userDto.getEmail());
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new ConflictException("Email " + userDto.getEmail() + " already exists");
        }

        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        User savedUser = userRepository.save(user);
        log.debug("User saved successfully: {}", savedUser);
        return toUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (userDto.getName() != null) user.setName(userDto.getName());
        if (userDto.getEmail() != null && !userRepository.existsByEmailAndIdNot(userDto.getEmail(), userId)) {
            user.setEmail(userDto.getEmail());
        } else if (userDto.getEmail() != null) {
            throw new ConflictException("Email already exists");
        }
        return toUserDto(userRepository.save(user));
    }

    @Override
    public Optional<UserDto> getUser(Long userId) {
        // Изменение: Возвращаем Optional<UserDto> напрямую, без выбрасывания исключения
        return userRepository.findById(userId).map(this::toUserDto);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserDto)
                .collect(Collectors.toList());
    }

    // Изменение: Добавлен метод для удаления пользователя
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    private UserDto toUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }
}