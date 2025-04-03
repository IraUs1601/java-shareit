package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.UnauthorizedException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit-тесты для ItemServiceImpl")
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private Item item;

    @BeforeEach
    void setup() {
        owner = new User(1L, "Owner", "owner@example.com");
        item = new Item(1L, "Item", "Description", true, owner, null);
    }

    @Test
    void createItem_shouldCreateSuccessfully() {
        ItemCreateDto dto = new ItemCreateDto();
        dto.setName("Item");
        dto.setDescription("Description");
        dto.setAvailable(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.createItem(dto, 1L);

        assertEquals("Item", result.getName());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void createItem_shouldThrowValidation_whenNameMissing() {
        ItemCreateDto dto = new ItemCreateDto();
        dto.setDescription("Description");
        dto.setAvailable(true);

        assertThrows(ValidationException.class, () -> itemService.createItem(dto, 1L));
    }

    @Test
    @DisplayName("getItem() should return item with bookings and comments if user is owner")
    void getItem_shouldReturnItemWithBookingsAndComments() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findLastBooking(eq(1L), any())).thenReturn(Optional.empty());
        when(bookingRepository.findNextBooking(eq(1L), any())).thenReturn(Optional.empty());
        when(commentRepository.findByItemId(1L)).thenReturn(List.of());

        ItemDto result = itemService.getItem(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Item", result.getName());

        verify(itemRepository).findById(1L);
        verify(commentRepository).findByItemId(1L);
    }

    @Test
    @DisplayName("getItems() должен вернуть список вещей владельца")
    void getItems_shouldReturnOwnedItems() {
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item));

        List<ItemDto> result = itemService.getItems(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item.getId(), result.get(0).getId());
        verify(itemRepository).findAllByOwnerId(1L);
    }

    @Test
    @DisplayName("addComment() должен сохранить комментарий, если пользователь бронировал вещь")
    void addComment_shouldAddSuccessfully() {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Great item!");

        Comment savedComment = new Comment(1L, "Great item!", item, owner, LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of(mock(Booking.class)));
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentDto result = itemService.addComment(1L, 1L, dto);

        assertNotNull(result);
        assertEquals("Great item!", result.getText());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void addComment_shouldThrow_whenUserDidNotBookItem() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBefore(
                anyLong(), anyLong(), any(), any())).thenReturn(List.of());

        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Nice");

        assertThrows(ValidationException.class, () -> itemService.addComment(1L, 1L, dto));
    }

    @Test
    @DisplayName("updateItem() should update item fields")
    void updateItem_shouldUpdateSuccessfully() {
        ItemUpdateDto updateDto = new ItemUpdateDto();
        updateDto.setName("Updated Name");
        updateDto.setDescription("Updated Desc");
        updateDto.setAvailable(false);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto result = itemService.updateItem(1L, updateDto, 1L);

        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Desc", result.getDescription());
        assertFalse(result.getAvailable());

        verify(itemRepository).findById(1L);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void deleteItem_shouldDeleteSuccessfully() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        itemService.deleteItem(1L, 1L);

        verify(itemRepository).delete(item);
    }

    @Test
    void deleteItem_shouldThrowUnauthorized() {
        item.setOwner(new User(2L, "Other", "o@example.com"));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(UnauthorizedException.class, () -> itemService.deleteItem(1L, 1L));
    }

    @Test
    void searchItems_shouldReturnList_whenTextValid() {
        when(itemRepository.searchAvailableItems("text"))
                .thenReturn(List.of(item));

        List<ItemDto> results = itemService.searchItems("text");

        assertEquals(1, results.size());
        assertEquals("Item", results.get(0).getName());
    }

    @Test
    void searchItems_shouldReturnEmpty_whenTextEmpty() {
        List<ItemDto> results = itemService.searchItems("   ");
        assertTrue(results.isEmpty());
    }
}