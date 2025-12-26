package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    // разрешил ли аренду вещи владелец
    @NotNull
    private Boolean available;
    // счетчик количества раз, сколько вещь была в аренде
    private Integer rentItemCount;
}

