package com.k2bot.ai.chatbot.persistence.converter;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StringListJsonConverterTest {

    private final StringListJsonConverter converter = new StringListJsonConverter();

    @Test
    void convertsListToJsonAndBack() {
        List<String> source = List.of("first", "second", "third");

        String json = converter.convertToDatabaseColumn(source);
        List<String> roundTrip = converter.convertToEntityAttribute(json);

        assertThat(json).isEqualTo("[\"first\",\"second\",\"third\"]");
        assertThat(roundTrip).containsExactly("first", "second", "third");
    }

    @Test
    void returnsNullForEmptyList() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToDatabaseColumn(List.of())).isNull();
    }

    @Test
    void returnsEmptyListForBlankColumn() {
        assertThat(converter.convertToEntityAttribute(null)).isEmpty();
        assertThat(converter.convertToEntityAttribute("")).isEmpty();
        assertThat(converter.convertToEntityAttribute("  ")).isEmpty();
    }
}
