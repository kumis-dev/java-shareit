package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto dto,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.create(dto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId,
                       @RequestBody ItemDto newDto,
                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.update(itemId, newDto, userId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable(name = "itemId") Long id,
                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemService.delete(id, userId);
    }

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findAll(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@PathVariable(name = "itemId") Long id) {
        return itemService.findById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }
}
