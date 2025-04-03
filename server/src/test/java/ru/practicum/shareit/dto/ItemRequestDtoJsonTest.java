package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("Тесты на сериализацию и десериализацию ItemRequest DTO")
public class ItemRequestDtoJsonTest {
    @Autowired
    private ObjectMapper objectMapper;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final LocalDateTime created = LocalDateTime.now();
    private final String createdString = formatter.format(created);

    @Test
    @DisplayName("Проверка сериализации ItemRequestCreateDto")
    void shouldSerializeItemRequestCreateDto() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Нужен ноутбук на неделю");

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"description\":\"Нужен ноутбук на неделю\"");
    }

    @Test
    @DisplayName("Проверка десериализации ItemRequestCreateDto")
    void shouldDeserializeItemRequestCreateDto() throws Exception {
        String json = "{\"description\":\"Нужен ноутбук на неделю\"}";

        ItemRequestCreateDto dto = objectMapper.readValue(json, ItemRequestCreateDto.class);

        assertThat(dto.getDescription()).isEqualTo("Нужен ноутбук на неделю");
    }

    @Test
    @DisplayName("Проверка сериализации ItemRequestDto")
    void shouldSerializeItemRequestDto() throws Exception {
        ItemRequestDto dto = new ItemRequestDto(
                1L,
                "Нужен ноутбук",
                created,
                List.of(new ItemDto(1L, "Ноутбук", "Игровой ноут", true, null))
        );

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"description\":\"Нужен ноутбук\"");
        assertThat(json).contains("\"created\":\"" + createdString + "\"");
        assertThat(json).contains("\"items\":[{\"id\":1");
    }

    @Test
    @DisplayName("Проверка десериализации ItemRequestDto")
    void shouldDeserializeItemRequestDto() throws Exception {
        String json = String.format(
                "{\"id\":1,\"description\":\"Нужен ноутбук\",\"created\":\"%s\",\"items\":[{\"id\":1,\"name\":\"Ноутбук\",\"description\":\"Игровой ноут\",\"available\":true}]}",
                createdString
        );

        ItemRequestDto dto = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Нужен ноутбук");
        assertThat(dto.getCreated()).isEqualTo(created);
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().get(0).getId()).isEqualTo(1L);
    }
}