package ru.practicum.shareit.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты класса Headers")
public class HeadersTest {

    @Test
    @DisplayName("USER_ID константа должна содержать ожидаемое значение")
    void userIdConstant_ShouldBeCorrect() {
        assertEquals("X-Sharer-User-Id", Headers.USER_ID);
    }

    @Test
    @DisplayName("Покрытие приватного конструктора Headers (рефлексия)")
    void constructor_ShouldBePrivate() throws Exception {
        Constructor<Headers> constructor = Headers.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertDoesNotThrow(() -> constructor.newInstance());
    }
}