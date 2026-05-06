package com.k2bot.ai.chatbot.memory.model;

import com.k2bot.ai.chatbot.config.model.ResolvedMemoryConfig;

public final class ChatMemoryContext {

    private final String sessionId;
    private final ResolvedMemoryConfig memoryConfig;

    public ChatMemoryContext(String sessionId, ResolvedMemoryConfig memoryConfig) {
        this.sessionId = sessionId;
        this.memoryConfig = memoryConfig;
    }

    public String getSessionId() {
        return sessionId;
    }

    public ResolvedMemoryConfig getMemoryConfig() {
        return memoryConfig;
    }
}
