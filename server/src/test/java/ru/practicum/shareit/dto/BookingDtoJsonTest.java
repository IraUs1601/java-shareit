package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("Тесты на сериализацию и десериализацию BookingDto и BookingCreateDto")
public class BookingDtoJsonTest {
    @Autowired
    private ObjectMapper objectMapper;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final LocalDateTime start = LocalDateTime.now().plusDays(1);
    private final LocalDateTime end = start.plusDays(2);
    private final String startString = formatter.format(start);
    private final String endString = formatter.format(end);

    @Test
    @DisplayName("Должен корректно сериализовать BookingDto в JSON")
    void shouldSerializeBookingDto() throws Exception {
        BookingDto dto = new BookingDto(
                1L, start, end,
                new ItemShortDto(1L, "Item"),
                new UserDto(1L, "User", "user@example.com"),
                Booking.BookingStatus.APPROVED
        );

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"start\":\"" + startString + "\"");
        assertThat(json).contains("\"end\":\"" + endString + "\"");
        assertThat(json).contains("\"item\":{\"id\":1,\"name\":\"Item\"}");
        assertThat(json).contains("\"booker\":{\"id\":1,\"name\":\"User\",\"email\":\"user@example.com\"}");
        assertThat(json).contains("\"status\":\"APPROVED\"");
    }

    @Test
    @DisplayName("Должен корректно десериализовать JSON в BookingDto")
    void shouldDeserializeBookingDto() throws Exception {
        String json = String.format(
                "{\"id\":1,\"start\":\"%s\",\"end\":\"%s\",\"item\":{\"id\":1,\"name\":\"Item\"},\"booker\":{\"id\":1,\"name\":\"User\",\"email\":\"user@example.com\"},\"status\":\"APPROVED\"}",
                startString, endString
        );

        BookingDto dto = objectMapper.readValue(json, BookingDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(start);
        assertThat(dto.getEnd()).isEqualTo(end);
        assertThat(dto.getItem().getId()).isEqualTo(1L);
        assertThat(dto.getBooker().getId()).isEqualTo(1L);
        assertThat(dto.getStatus()).isEqualTo(Booking.BookingStatus.APPROVED);
    }

    @Test
    @DisplayName("Должен корректно сериализовать BookingCreateDto в JSON")
    void shouldSerializeBookingCreateDto() throws Exception {
        BookingCreateDto dto = new BookingCreateDto(1L, start, end);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"itemId\":1");
        assertThat(json).contains("\"start\":\"" + startString + "\"");
        assertThat(json).contains("\"end\":\"" + endString + "\"");
    }

    @Test
    @DisplayName("Должен корректно десериализовать JSON в BookingCreateDto")
    void shouldDeserializeBookingCreateDto() throws Exception {
        String json = String.format("{\"itemId\":1,\"start\":\"%s\",\"end\":\"%s\"}", startString, endString);

        BookingCreateDto dto = objectMapper.readValue(json, BookingCreateDto.class);

        assertThat(dto.getItemId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(start);
        assertThat(dto.getEnd()).isEqualTo(end);
    }
}