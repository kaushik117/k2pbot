package com.k2bot.ai.chatbot.memory.store;

import com.k2bot.ai.chatbot.memory.api.ChatMemory;
import com.k2bot.ai.chatbot.memory.model.ChatMemoryMessage;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class NoOpChatMemory implements ChatMemory {

    @Override
    public void add(String conversationId, List<ChatMemoryMessage> messages) {
    }

    @Override
    public List<ChatMemoryMessage> get(String conversationId, int lastN) {
        return Collections.emptyList();
    }

    @Override
    public void clear(String conversationId) {
    }
}
