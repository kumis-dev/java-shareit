package ru.practicum.shareit.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void shouldSerializeWithCreatedField() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Need a drill");
        dto.setCreateTime(LocalDateTime.of(2024, 1, 1, 12, 0));

        var result = json.write(dto);

        // Проверяем что поле createTime сериализуется как "created"
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Need a drill");
        assertThat(result).hasJsonPathValue("$.created");
        assertThat(result).doesNotHaveJsonPathValue("$.createTime");
    }
}
