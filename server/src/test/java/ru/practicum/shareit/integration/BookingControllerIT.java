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
@DisplayName("Интеграционные тесты контроллера бронирований")
public class BookingControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql("/sql/testing_db.sql")
    @DisplayName("Корректное возвращение списка бронирований при запросе")
    void findBookings_ReturnsBookingList() throws Exception {
        long userId = 5L;
        var requestBuilder = MockMvcRequestBuilders.get("/bookings")
                .header("X-Sharer-User-Id", userId);

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                    [{"id":1,"start":"2023-07-20T12:00:00","end":"2023-07-25T12:00:00",
                    "item":{"id":10,"name":"NAME"},
                    "booker":{"id":5,"name":"Иван Иванов","email":"ivan@example.com"},
                    "status":"APPROVED"}]
                """));
    }
}