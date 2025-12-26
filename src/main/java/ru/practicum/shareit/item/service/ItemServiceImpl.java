package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemDto create(ItemDto dto, Long userId) {
        // проверяем существование пользователя
        userService.findById(userId);

        Item item = ItemMapper.mapToItem(dto);
        item.setUserId(userId);
        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long itemId, ItemDto newDto, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.getUserId().equals(userId))  // проверяем существующего владельца
            throw new ForbiddenException("Редактировать может только владелец");

        Item newItem = ItemMapper.mapToItem(newDto);
        // обновляем поля у item новыми полями из newItem
        ItemMapper.updateFields(item, newItem);
        return ItemMapper.mapToItemDto(itemRepository.update(item));
    }

    @Override
    public void delete(Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.getUserId().equals(userId))  // проверяем существующего владельца
            throw new ForbiddenException("Удалять может только владелец");

        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> findAll(Long userId) {
        return itemRepository.findAllByUserId(userId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto findById(Long id) {
        return ItemMapper.mapToItemDto(
                itemRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Вещь не найдена")));
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemRepository.search(text)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }
}
