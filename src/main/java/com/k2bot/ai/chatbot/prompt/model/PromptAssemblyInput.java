package com.k2bot.ai.chatbot.prompt.model;

import com.k2bot.ai.chatbot.config.model.ResolvedAssistantConfig;
import com.k2bot.ai.chatbot.modelrouting.model.ModelSelectionResult;
import com.k2bot.ai.chatbot.orchestration.model.ChatRequest;

import java.util.Map;

public class PromptAssemblyInput {

    private final ChatRequest chatRequest;
    private final ResolvedAssistantConfig resolvedConfig;
    private final ModelSelectionResult modelSelectionResult;
    private final Map<String, Object> resolvedRuntimeVariables;

    public PromptAssemblyInput(
            ChatRequest chatRequest,
            ResolvedAssistantConfig resolvedConfig,
            ModelSelectionResult modelSelectionResult,
            Map<String, Object> resolvedRuntimeVariables) {
        this.chatRequest = chatRequest;
        this.resolvedConfig = resolvedConfig;
        this.modelSelectionResult = modelSelectionResult;
        this.resolvedRuntimeVariables = resolvedRuntimeVariables;
    }

    public ChatRequest getChatRequest() {
        return chatRequest;
    }

    public ResolvedAssistantConfig getResolvedConfig() {
        return resolvedConfig;
    }

    public ModelSelectionResult getModelSelectionResult() {
        return modelSelectionResult;
    }

    public Map<String, Object> getResolvedRuntimeVariables() {
        return resolvedRuntimeVariables;
    }
}
