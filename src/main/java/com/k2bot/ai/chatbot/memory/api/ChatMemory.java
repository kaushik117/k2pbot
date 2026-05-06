package com.k2bot.ai.chatbot.memory.api;

import com.k2bot.ai.chatbot.memory.model.ChatMemoryMessage;

import java.util.List;

public interface ChatMemory {

    void add(String conversationId, List<ChatMemoryMessage> messages);

    List<ChatMemoryMessage> get(String conversationId, int lastN);

    void clear(String conversationId);
}
