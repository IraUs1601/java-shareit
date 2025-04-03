package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("Тестирование JSON сериализации и десериализации CommentDto")
public class CommentDtoJsonTest {
    @Autowired
    private ObjectMapper objectMapper;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final LocalDateTime created = LocalDateTime.now();
    private final String createdString = formatter.format(created);

    @Test
    @DisplayName("Должен корректно сериализовать CommentDto в JSON")
    void shouldSerializeCommentDto() throws Exception {
        CommentDto dto = new CommentDto(
                1L,
                "Great item!",
                "Author Name",
                created
        );

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"text\":\"Great item!\"");
        assertThat(json).contains("\"authorName\":\"Author Name\"");
        assertThat(json).contains("\"created\":\"" + createdString + "\"");
    }

    @Test
    @DisplayName("Должен корректно десериализовать JSON в CommentDto")
    void shouldDeserializeCommentDto() throws Exception {
        String json = String.format(
                "{\"id\":1,\"text\":\"Great item!\",\"authorName\":\"Author Name\",\"created\":\"%s\"}",
                createdString
        );

        CommentDto dto = objectMapper.readValue(json, CommentDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("Great item!");
        assertThat(dto.getAuthorName()).isEqualTo("Author Name");
        assertThat(dto.getCreated()).isEqualTo(created);
    }
}