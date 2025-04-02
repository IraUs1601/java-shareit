package ru.practicum.shareit.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("Тест JSON-сериализации ErrorResponse")
public class ErrorResponseJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Сериализация ErrorResponse")
    void shouldSerializeErrorResponse() throws Exception {
        ErrorResponse response = new ErrorResponse("Ошибка валидации");

        String json = objectMapper.writeValueAsString(response);

        assertThat(json).contains("\"error\":\"Ошибка валидации\"");
    }
}