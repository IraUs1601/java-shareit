package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты для CommentMapper")
public class CommentMapperTest {

    @Test
    @DisplayName("Преобразование Comment в CommentDto")
    void toCommentDto_ShouldMapCorrectly() {
        User author = new User(1L, "John Doe", "john@example.com");
        Item item = new Item();
        item.setId(10L);

        Comment comment = new Comment(100L, "Great item!", item, author, LocalDateTime.now());

        CommentDto dto = CommentMapper.toCommentDto(comment);

        assertNotNull(dto);
        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getText(), dto.getText());
        assertEquals(author.getName(), dto.getAuthorName());
        assertEquals(comment.getCreated(), dto.getCreated());
    }
}