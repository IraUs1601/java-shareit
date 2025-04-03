package ru.practicum.shareit.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Unit-тесты для GlobalExceptionHandler")
public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Обработка NotFoundException")
    void handleNotFoundException() {
        NotFoundException ex = new NotFoundException("Not found");
        ResponseEntity<ErrorResponse> response = handler.handleNotFoundException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getError()).isEqualTo("Not found");
    }

    @Test
    @DisplayName("Обработка UnauthorizedException")
    void handleUnauthorizedException() {
        UnauthorizedException ex = new UnauthorizedException("Access denied");
        ResponseEntity<ErrorResponse> response = handler.handleUnauthorizedException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getError()).isEqualTo("Access denied");
    }

    @Test
    @DisplayName("Обработка ConflictException")
    void handleConflictException() {
        ConflictException ex = new ConflictException("Email exists");
        ResponseEntity<ErrorResponse> response = handler.handleConflictException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getError()).isEqualTo("Email exists");
    }

    @Test
    @DisplayName("Обработка MissingRequestHeaderException")
    void handleMissingRequestHeaderException() {
        MissingRequestHeaderException ex = new MissingRequestHeaderException("X-Sharer-User-Id", null);
        ResponseEntity<ErrorResponse> response = handler.handleMissingHeaderException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getError()).contains("X-Sharer-User-Id");
    }

    @Test
    @DisplayName("Обработка ValidationException")
    void handleValidationException() {
        ValidationException ex = new ValidationException("Validation failed");
        ResponseEntity<ErrorResponse> response = handler.handleValidationException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getError()).isEqualTo("Validation failed");
    }

    @Test
    @DisplayName("Обработка общего исключения")
    void handleGeneralException() {
        Exception ex = new RuntimeException("Something went wrong");
        ResponseEntity<ErrorResponse> response = handler.handleGeneralException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getError()).contains("Something went wrong");
    }
}