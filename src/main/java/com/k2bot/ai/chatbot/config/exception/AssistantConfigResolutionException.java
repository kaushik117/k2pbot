package com.k2bot.ai.chatbot.config.exception;

public class AssistantConfigResolutionException extends RuntimeException {

    private final String assistantCode;

    public AssistantConfigResolutionException(String assistantCode, String message) {
        super("Config resolution failed for assistantCode='" + assistantCode + "': " + message);
        this.assistantCode = assistantCode;
    }

    public AssistantConfigResolutionException(String assistantCode, String message, Throwable cause) {
        super("Config resolution failed for assistantCode='" + assistantCode + "': " + message, cause);
        this.assistantCode = assistantCode;
    }

    public String getAssistantCode() {
        return assistantCode;
    }
}
