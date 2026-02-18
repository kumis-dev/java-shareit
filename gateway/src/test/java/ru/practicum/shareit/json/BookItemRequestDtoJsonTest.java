package ru.practicum.shareit.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<BookItemRequestDto> json;

    @Test
    void shouldSerializeBookItemRequestDto() throws Exception {
        BookItemRequestDto dto = new BookItemRequestDto(
                1L,
                LocalDateTime.of(2024, 1, 1, 12, 0),
                LocalDateTime.of(2024, 1, 2, 12, 0)
        );

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2024-01-01T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2024-01-02T12:00:00");
    }
}
