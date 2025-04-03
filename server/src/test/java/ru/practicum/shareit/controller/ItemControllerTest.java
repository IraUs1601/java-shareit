package ru.practicum.shareit.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnauthorizedException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты ItemController")
public class ItemControllerTest {
    @Mock
    private ItemServiceImpl itemService;
    @InjectMocks
    private ItemController itemController;

    private ItemDto createTestItemDto(Long id) {
        return new ItemDto(
                id,
                "Item " + id,
                "Description " + id,
                true,
                null,
                null,
                null,
                List.of()
        );
    }

    private ItemCreateDto createTestItemCreateDto() {
        ItemCreateDto dto = new ItemCreateDto();
        dto.setName("Test Item");
        dto.setDescription("Test Description");
        dto.setAvailable(true);
        return dto;
    }

    private CommentDto createTestCommentDto(Long id) {
        return new CommentDto(
                id,
                "Comment " + id,
                "Author " + id,
                LocalDateTime.now()
        );
    }

    private CommentCreateDto createTestCommentCreateDto() {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Test comment");
        return dto;
    }

    @Test
    @DisplayName("Создание предмета - успешно")
    void createItem_ValidData_ReturnsCreated() {
        long userId = 1L;
        ItemCreateDto createDto = createTestItemCreateDto();
        ItemDto expectedDto = createTestItemDto(1L);

        doReturn(expectedDto)
                .when(this.itemService)
                .createItem(any(ItemCreateDto.class), anyLong());

        var result = this.itemController.createItem(userId, createDto);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(expectedDto, result.getBody());
        verify(this.itemService).createItem(createDto, userId);
        verifyNoMoreInteractions(this.itemService);
    }

    @Test
    @DisplayName("Получение предмета по ID - успешно")
    void getItem_ValidRequest_ReturnsItem() {
        long userId = 1L;
        long itemId = 1L;
        ItemDto expectedDto = createTestItemDto(itemId);

        doReturn(expectedDto)
                .when(this.itemService)
                .getItem(userId, itemId);

        var result = this.itemController.getItem(userId, itemId);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(expectedDto, result.getBody());
        verify(this.itemService).getItem(userId, itemId);
        verifyNoMoreInteractions(this.itemService);
    }

    @Test
    @DisplayName("Получение предмета по ID - не найден")
    void getItem_NotFound_ThrowsException() {
        long userId = 1L;
        long itemId = 999L;

        doThrow(new NotFoundException("Item not found"))
                .when(this.itemService)
                .getItem(userId, itemId);

        assertThrows(NotFoundException.class,
                () -> this.itemController.getItem(userId, itemId));
        verify(this.itemService).getItem(userId, itemId);
        verifyNoMoreInteractions(this.itemService);
    }

    @Test
    @DisplayName("Добавление комментария - успешно")
    void addComment_ValidData_ReturnsCreated() {
        long userId = 1L;
        long itemId = 1L;
        CommentCreateDto createDto = createTestCommentCreateDto();
        CommentDto expectedDto = createTestCommentDto(1L);

        doReturn(expectedDto)
                .when(this.itemService)
                .addComment(userId, itemId, createDto);

        var result = this.itemController.addComment(userId, itemId, createDto);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(expectedDto, result.getBody());
        verify(this.itemService).addComment(userId, itemId, createDto);
        verifyNoMoreInteractions(this.itemService);
    }

    @Test
    @DisplayName("Получение списка предметов - по владельцу")
    void getItems_WithOwner_ReturnsList() {
        long ownerId = 1L;
        List<ItemDto> expectedList = List.of(
                createTestItemDto(1L),
                createTestItemDto(2L)
        );

        doReturn(expectedList)
                .when(this.itemService)
                .getItems(ownerId);

        var result = this.itemController.getItems(ownerId);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(2, Objects.requireNonNull(result.getBody()).size());
        verify(this.itemService).getItems(ownerId);
        verifyNoMoreInteractions(this.itemService);
    }

    @Test
    @DisplayName("Получение списка предметов - все предметы")
    void getItems_WithoutOwner_ReturnsAll() {
        List<ItemDto> expectedList = List.of(
                createTestItemDto(1L),
                createTestItemDto(2L)
        );

        doReturn(expectedList)
                .when(this.itemService)
                .getItems(null);

        var result = this.itemController.getItems(null);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(2, Objects.requireNonNull(result.getBody()).size());
        verify(this.itemService).getItems(null);
        verifyNoMoreInteractions(this.itemService);
    }

    @Test
    @DisplayName("Получение предметов пользователя - неверный ID")
    void getUserItems_InvalidId_ThrowsException() {
        String invalidId = "abc";

        assertThrows(ValidationException.class,
                () -> this.itemController.getUserItems(invalidId));
        verifyNoInteractions(this.itemService);
    }

    @Test
    @DisplayName("Получение предметов пользователя - корректный ID")
    void getUserItems_ValidId_ReturnsItems() {
        String validId = "1";
        List<ItemDto> expected = List.of(createTestItemDto(1L));

        doReturn(expected)
                .when(itemService)
                .getItems(1L);

        var result = itemController.getUserItems(validId);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(expected, result.getBody());
        verify(itemService).getItems(1L);
    }

    @Test
    @DisplayName("Поиск предметов - успешно")
    void searchItems_ValidQuery_ReturnsList() {
        String query = "test";
        List<ItemDto> expectedList = List.of(
                createTestItemDto(1L),
                createTestItemDto(2L)
        );

        doReturn(expectedList)
                .when(this.itemService)
                .searchItems(query);

        var result = this.itemController.searchItems(query);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals(2, Objects.requireNonNull(result.getBody()).size());
        verify(this.itemService).searchItems(query);
        verifyNoMoreInteractions(this.itemService);
    }

    @Test
    @DisplayName("Обновление предмета - успешно")
    void updateItem_ValidData_ReturnsUpdated() {
        long userId = 1L;
        long itemId = 1L;
        ItemUpdateDto updateDto = new ItemUpdateDto();
        updateDto.setName("Updated Name");
        ItemDto expectedDto = createTestItemDto(itemId);
        expectedDto.setName("Updated Name");

        doReturn(expectedDto)
                .when(this.itemService)
                .updateItem(itemId, updateDto, userId);

        var result = this.itemController.updateItem(userId, itemId, updateDto);

        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertEquals("Updated Name", Objects.requireNonNull(result.getBody()).getName());
        verify(this.itemService).updateItem(itemId, updateDto, userId);
        verifyNoMoreInteractions(this.itemService);
    }

    @Test
    @DisplayName("Удаление предмета - успешно")
    void deleteItem_ValidRequest_ReturnsNoContent() {
        long userId = 1L;
        long itemId = 1L;

        doNothing()
                .when(this.itemService)
                .deleteItem(itemId, userId);

        var result = this.itemController.deleteItem(userId, itemId);

        assertEquals(204, result.getStatusCodeValue());
        verify(this.itemService).deleteItem(itemId, userId);
        verifyNoMoreInteractions(this.itemService);
    }

    @Test
    @DisplayName("Удаление предмета - не авторизован")
    void deleteItem_Unauthorized_ThrowsException() {
        long userId = 2L;
        long itemId = 1L;

        doThrow(new UnauthorizedException("Not authorized"))
                .when(this.itemService)
                .deleteItem(itemId, userId);

        assertThrows(UnauthorizedException.class,
                () -> this.itemController.deleteItem(userId, itemId));
        verify(this.itemService).deleteItem(itemId, userId);
        verifyNoMoreInteractions(this.itemService);
    }
}