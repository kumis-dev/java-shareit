package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto dto, Long userId);

    ItemDto update(Long itemId, ItemDto newDto, Long userId);

    void delete(Long id, Long userId);

    List<ItemDto> findAll(Long userId);

    ItemDto findById(Long id);

    List<ItemDto> search(String text);
}
