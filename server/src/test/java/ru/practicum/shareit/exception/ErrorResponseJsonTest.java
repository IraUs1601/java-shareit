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

    @Test
    @DisplayName("equals и hashCode работают корректно (по содержимому)")
    void errorResponseEqualsHashCode() {
        ErrorResponse e1 = new ErrorResponse("Ошибка");
        ErrorResponse e2 = new ErrorResponse("Ошибка");

        assertThat(e1.getError()).isEqualTo(e2.getError());
        assertThat(e1.getError().hashCode()).isEqualTo(e2.getError().hashCode());
    }

    @Test
    void getError_shouldReturnErrorMessage() {
        ErrorResponse error = new ErrorResponse("Test error");
        assertThat(error.getError()).isEqualTo("Test error");
    }
}