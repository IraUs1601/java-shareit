package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("Тесты сериализации и десериализации User DTO")
public class UserDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Должен корректно сериализовать UserDto")
    void shouldSerializeUserDto() throws Exception {
        UserDto userDto = new UserDto(1L, "John Doe", "john.doe@example.com");

        String json = objectMapper.writeValueAsString(userDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"John Doe\"");
        assertThat(json).contains("\"email\":\"john.doe@example.com\"");
    }

    @Test
    @DisplayName("Должен корректно десериализовать UserDto")
    void shouldDeserializeUserDto() throws Exception {
        String json = "{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";

        UserDto userDto = objectMapper.readValue(json, UserDto.class);

        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getName()).isEqualTo("John Doe");
        assertThat(userDto.getEmail()).isEqualTo("john.doe@example.com");
    }
}