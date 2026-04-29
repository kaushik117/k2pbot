package com.k2bot.ai.chatbot.persistence.converter;

import tools.jackson.databind.ObjectMapper;

final class PersistenceObjectMapper {

    private static final ObjectMapper INSTANCE = new ObjectMapper();

    private PersistenceObjectMapper() {
    }

    static ObjectMapper get() {
        return INSTANCE;
    }
}
