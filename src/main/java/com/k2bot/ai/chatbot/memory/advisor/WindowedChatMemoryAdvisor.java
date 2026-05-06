package com.k2bot.ai.chatbot.memory.advisor;

import com.k2bot.ai.chatbot.memory.api.ChatMemory;
import com.k2bot.ai.chatbot.memory.api.ChatMemoryAdvisor;
import com.k2bot.ai.chatbot.memory.model.ChatMemoryMessage;
import com.k2bot.ai.chatbot.memory.support.MessageWindowHelper;
import com.k2bot.ai.chatbot.persistence.entity.MemoryStoreType;

import java.util.Collections;
import java.util.List;

public class WindowedChatMemoryAdvisor implements ChatMemoryAdvisor {

    private final ChatMemory chatMemory;
    private final int windowSize;

    public WindowedChatMemoryAdvisor(ChatMemory chatMemory, int windowSize) {
        this.chatMemory = chatMemory;
        this.windowSize = windowSize;
    }

    @Override
    public List<ChatMemoryMessage> loadHistory(String sessionId, int maxMessages) {
        int effectiveLimit = MessageWindowHelper.effectiveWindowSize(windowSize, maxMessages);
        return chatMemory.get(sessionId, effectiveLimit);
    }

    @Override
    public void recordMessage(String sessionId, ChatMemoryMessage message) {
        chatMemory.add(sessionId, Collections.singletonList(message));
    }

    @Override
    public MemoryStoreType getStoreType() {
        return MemoryStoreType.IN_MEMORY;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public int getWindowSize() {
        return windowSize;
    }
}
