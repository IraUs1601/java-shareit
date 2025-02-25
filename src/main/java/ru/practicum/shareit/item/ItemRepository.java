package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long idCounter = 1;

    public Item save(Item item) {
        item.setId(idCounter++);
        items.put(item.getId(), item);
        return item;
    }

    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    public List<Item> findAllByOwner(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    public List<Item> search(String text) {
        String lowerText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(lowerText)
                        || item.getDescription().toLowerCase().contains(lowerText))
                        && item.isAvailable())
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        items.remove(id);
    }
}