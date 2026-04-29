package com.k2bot.ai.chatbot.persistence.converter;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JsonMapConverterTest {

    private final JsonMapConverter converter = new JsonMapConverter();

    @Test
    void convertsMapToJsonAndBack() {
        Map<String, String> source = new LinkedHashMap<>();
        source.put("locale", "en-US");
        source.put("channel", "web");

        String json = converter.convertToDatabaseColumn(source);
        Map<String, String> roundTrip = converter.convertToEntityAttribute(json);

        assertThat(json).contains("\"locale\":\"en-US\"");
        assertThat(roundTrip).containsExactlyInAnyOrderEntriesOf(source);
    }

    @Test
    void returnsNullForEmptyMap() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToDatabaseColumn(Map.of())).isNull();
    }

    @Test
    void returnsEmptyMapForBlankColumn() {
        assertThat(converter.convertToEntityAttribute(null)).isEmpty();
        assertThat(converter.convertToEntityAttribute("")).isEmpty();
        assertThat(converter.convertToEntityAttribute("   ")).isEmpty();
    }
}
