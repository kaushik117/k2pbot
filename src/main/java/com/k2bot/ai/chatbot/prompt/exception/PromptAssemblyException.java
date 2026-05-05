package com.k2bot.ai.chatbot.prompt.exception;

public class PromptAssemblyException extends RuntimeException {

    public PromptAssemblyException(String message) {
        super(message);
    }

    public PromptAssemblyException(String message, Throwable cause) {
        super(message, cause);
    }
}
