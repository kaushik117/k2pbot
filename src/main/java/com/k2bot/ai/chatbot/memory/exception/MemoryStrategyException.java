package com.k2bot.ai.chatbot.memory.exception;

public class MemoryStrategyException extends RuntimeException {

    public MemoryStrategyException(String message) {
        super(message);
    }

    public MemoryStrategyException(String message, Throwable cause) {
        super(message, cause);
    }
}
