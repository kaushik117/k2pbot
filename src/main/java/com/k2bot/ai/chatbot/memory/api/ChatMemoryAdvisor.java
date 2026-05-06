package com.k2bot.ai.chatbot.memory.api;

import com.k2bot.ai.chatbot.memory.model.ChatMemoryMessage;
import com.k2bot.ai.chatbot.persistence.entity.MemoryStoreType;

import java.util.List;

public interface ChatMemoryAdvisor {

    List<ChatMemoryMessage> loadHistory(String sessionId, int maxMessages);

    void recordMessage(String sessionId, ChatMemoryMessage message);

    MemoryStoreType getStoreType();

    boolean isEnabled();
}
