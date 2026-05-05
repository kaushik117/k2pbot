package com.k2bot.ai.chatbot.config.model;

import com.k2bot.ai.chatbot.persistence.entity.MemoryStoreType;

public final class ResolvedMemoryConfig {

    private final boolean enabled;
    private final MemoryStoreType storeType;
    private final Integer messageWindowSize;
    private final Integer ttlMinutes;
    private final boolean persistChatHistory;
    private final boolean summarizeOldMessages;

    public ResolvedMemoryConfig(
            boolean enabled,
            MemoryStoreType storeType,
            Integer messageWindowSize,
            Integer ttlMinutes,
            boolean persistChatHistory,
            boolean summarizeOldMessages) {
        this.enabled = enabled;
        this.storeType = storeType;
        this.messageWindowSize = messageWindowSize;
        this.ttlMinutes = ttlMinutes;
        this.persistChatHistory = persistChatHistory;
        this.summarizeOldMessages = summarizeOldMessages;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public MemoryStoreType getStoreType() {
        return storeType;
    }

    public Integer getMessageWindowSize() {
        return messageWindowSize;
    }

    public Integer getTtlMinutes() {
        return ttlMinutes;
    }

    public boolean isPersistChatHistory() {
        return persistChatHistory;
    }

    public boolean isSummarizeOldMessages() {
        return summarizeOldMessages;
    }
}
