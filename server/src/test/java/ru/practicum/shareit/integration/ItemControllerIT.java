package ru.practicum.shareit.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Интеграционные тесты контроллера items")
public class ItemControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql("/sql/testing_db.sql")
    @DisplayName("Корректное создание нового item")
    void createItem_ReturnsNewItem() throws Exception {
        long userId = 1L;
        var requestBuilder = MockMvcRequestBuilders.post("/items")
                .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        String.join("\n",
                                "{",
                                "  \"id\": 1,",
                                "  \"name\": \"Какой-то item\",",
                                "  \"description\": \"description\",",
                                "  \"available\": \"true\",",
                                "  \"requestId\": 1",
                                "}"
                        )
                );

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(
                                String.join("\n",
                                        "{",
                                        "  \"id\": 1,",
                                        "  \"name\": \"Какой-то item\",",
                                        "  \"description\": \"description\",",
                                        "  \"available\": true,",
                                        "  \"requestId\": 1,",
                                        "  \"lastBooking\": null,",
                                        "  \"nextBooking\": null,",
                                        "  \"comments\": []",
                                        "}"
                                )
                        )
                );
    }
}