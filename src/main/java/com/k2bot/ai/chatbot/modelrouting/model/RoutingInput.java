package com.k2bot.ai.chatbot.modelrouting.model;

import com.k2bot.ai.chatbot.config.model.ResolvedAssistantConfig;
import com.k2bot.ai.chatbot.orchestration.model.ChatRequest;

public class RoutingInput {

    private final ChatRequest chatRequest;
    private final ResolvedAssistantConfig resolvedConfig;

    public RoutingInput(ChatRequest chatRequest, ResolvedAssistantConfig resolvedConfig) {
        this.chatRequest = chatRequest;
        this.resolvedConfig = resolvedConfig;
    }

    public ChatRequest getChatRequest() {
        return chatRequest;
    }

    public ResolvedAssistantConfig getResolvedConfig() {
        return resolvedConfig;
    }
}
