package com.k2bot.ai.chatbot.persistence.exception;

import com.k2bot.ai.chatbot.common.ChatbotException;

public class PersistenceLayerException extends ChatbotException {

    public PersistenceLayerException(String message) {
        super(message);
    }

    public PersistenceLayerException(String message, Throwable cause) {
        super(message, cause);
    }
}
