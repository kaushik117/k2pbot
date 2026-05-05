package com.k2bot.ai.chatbot.config.exception;

public class UnauthorizedRuntimeOverrideException extends RuntimeException {

    private final String field;

    public UnauthorizedRuntimeOverrideException(String field) {
        super("Runtime override not permitted for field: " + field);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
