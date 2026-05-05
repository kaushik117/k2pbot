package com.k2bot.ai.chatbot.config.exception;

public class AssistantConfigValidationException extends RuntimeException {

    private final String assistantCode;

    public AssistantConfigValidationException(String assistantCode, String message) {
        super("Config validation failed for assistantCode='" + assistantCode + "': " + message);
        this.assistantCode = assistantCode;
    }

    public AssistantConfigValidationException(String assistantCode, String message, Throwable cause) {
        super("Config validation failed for assistantCode='" + assistantCode + "': " + message, cause);
        this.assistantCode = assistantCode;
    }

    public String getAssistantCode() {
        return assistantCode;
    }
}
