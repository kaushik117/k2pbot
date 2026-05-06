package com.k2bot.ai.chatbot.memory.api;

import com.k2bot.ai.chatbot.memory.model.ChatMemoryContext;

import java.util.List;

public interface MemoryAdvisorFactory {

    List<ChatMemoryAdvisor> create(ChatMemoryContext context);
}
