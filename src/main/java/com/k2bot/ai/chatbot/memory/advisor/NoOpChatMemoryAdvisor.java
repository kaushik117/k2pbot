package com.k2bot.ai.chatbot.memory.advisor;

import com.k2bot.ai.chatbot.memory.api.ChatMemoryAdvisor;
import com.k2bot.ai.chatbot.memory.model.ChatMemoryMessage;
import com.k2bot.ai.chatbot.persistence.entity.MemoryStoreType;

import java.util.Collections;
import java.util.List;

public class NoOpChatMemoryAdvisor implements ChatMemoryAdvisor {

    @Override
    public List<ChatMemoryMessage> loadHistory(String sessionId, int maxMessages) {
        return Collections.emptyList();
    }

    @Override
    public void recordMessage(String sessionId, ChatMemoryMessage message) {
    }

    @Override
    public MemoryStoreType getStoreType() {
        return MemoryStoreType.NONE;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
