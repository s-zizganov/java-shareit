package ru.practicum.shareit.gateway.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JsonTest
class UserDtoTest {

    @Test
    void serializeUserDtoShouldSucceed() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        UserDto userDto = new UserDto(1L, "John", "john@example.com");
        String jsonString = objectMapper.writeValueAsString(userDto);
        Map<String, Object> jsonMap = objectMapper.readValue(jsonString, Map.class);
        assertThat(jsonMap.get("name"), is("John"));
        assertThat(jsonMap.get("email"), is("john@example.com"));
    }

    @Test
    void deserializeUserDto_withInvalidEmailShouldThrowException() throws JsonProcessingException {
        String jsonContent = "{\"id\": 1, \"name\": \"John\", \"email\": \"invalid-email\"}";
        ObjectMapper objectMapper = new ObjectMapper();
        UserDto userDto = objectMapper.readValue(jsonContent, UserDto.class);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        assertThrows(ConstraintViolationException.class, () -> {
            var violations = validator.validate(userDto);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
        });
    }
}