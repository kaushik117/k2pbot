package com.k2bot.ai.chatbot.memory.advisor;

import com.k2bot.ai.chatbot.memory.api.ChatMemory;
import com.k2bot.ai.chatbot.memory.api.ChatMemoryAdvisor;
import com.k2bot.ai.chatbot.memory.model.ChatMemoryMessage;
import com.k2bot.ai.chatbot.memory.support.MessageWindowHelper;
import com.k2bot.ai.chatbot.persistence.entity.MemoryStoreType;

import java.util.List;

public class JdbcChatMemoryAdvisor implements ChatMemoryAdvisor {

    private final ChatMemory chatMemory;
    private final int windowSize;

    public JdbcChatMemoryAdvisor(ChatMemory chatMemory, int windowSize) {
        this.chatMemory = chatMemory;
        this.windowSize = windowSize;
    }

    @Override
    public List<ChatMemoryMessage> loadHistory(String sessionId, int maxMessages) {
        int effectiveLimit = MessageWindowHelper.effectiveWindowSize(windowSize, maxMessages);
        return chatMemory.get(sessionId, effectiveLimit);
    }

    /**
     * No-op: JDBC-backed history is persisted by ConversationPersistenceService.
     */
    @Override
    public void recordMessage(String sessionId, ChatMemoryMessage message) {
    }

    @Override
    public MemoryStoreType getStoreType() {
        return MemoryStoreType.JDBC;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public int getWindowSize() {
        return windowSize;
    }
}
