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
@DisplayName("Интеграционные тесты контроллера itemRequest")
public class ItemRequestControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql("/sql/testing_db.sql")
    @DisplayName("Корректное возвращение запроса по id")
    void findRequestById_ReturnsTheRequest() throws Exception {
        long userId = 1L;
        var requestBuilder = MockMvcRequestBuilders.get("/requests/1")
                .header("X-Sharer-User-Id", userId);

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(
                                "{\"id\":1,\"description\":\"Нужна дрель для ремонта\",\"created\":\"2023-10-01T12:00:00\",\"items\":[]}"
                        )
                );
    }
}