package com.k2bot.ai.chatbot.modelrouting.exception;

public class ModelRoutingException extends RuntimeException {

    public ModelRoutingException(String message) {
        super(message);
    }

    public ModelRoutingException(String message, Throwable cause) {
        super(message, cause);
    }
}
