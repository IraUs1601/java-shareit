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
@DisplayName("Интеграционные тесты юзер контроллера")
public class UserControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql("/sql/testing_db.sql")
    @DisplayName("Корректное возвращение запроса по id")
    void findUsers_ReturnsUsersList() throws Exception {
        long userId = 1L;
        var requestBuilder = MockMvcRequestBuilders.get("/users")
                .header("X-Sharer-User-Id", userId);

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(
                                "[" +
                                        "{\"id\":1,\"name\":\"Тестовый Пользователь\",\"email\":\"test@example.com\"}," +
                                        "{\"id\":2,\"name\":\"Тестовый Пользователь 2\",\"email\":\"test2@example.com\"}," +
                                        "{\"id\":5,\"name\":\"Иван Иванов\",\"email\":\"ivan@example.com\"}," +
                                        "{\"id\":8,\"name\":\"Петр Петров\",\"email\":\"petr@example.com\"}" +
                                        "]"
                        )
                );
    }
}