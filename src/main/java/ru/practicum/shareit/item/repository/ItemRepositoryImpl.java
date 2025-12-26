package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    Map<Long, Item> items = new HashMap<>();
    private long nextId = 1;

    @Override
    public Item save(Item item) {
        item.setId(nextId++);

        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item newItem) {
        items.put(newItem.getId(), newItem);
        return newItem;
    }

    @Override
    public void deleteById(Long id) {
        items.remove(id);
    }

    @Override
    public List<Item> findAllByUserId(Long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> search(String text) {
        if (text == null || text.isBlank())
            return List.of();

        String lowText = text.toLowerCase();
        return items.values()
                .stream()
                .filter(Item::getAvailable) // only true for rent items
                .filter(item -> item.getName().toLowerCase().contains(lowText) ||
                        item.getDescription().toLowerCase().contains(lowText))
                .collect(Collectors.toList());
    }
}
