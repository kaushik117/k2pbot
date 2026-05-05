package com.k2bot.ai.chatbot.config.model;

import java.util.List;
import java.util.Map;

public final class ResolvedPromptConfig {

    private final String systemPromptTemplate;
    private final String developerPromptTemplate;
    private final Map<String, String> defaultVariables;
    private final List<String> guardrailInstructions;
    private final String promptVersion;

    public ResolvedPromptConfig(
            String systemPromptTemplate,
            String developerPromptTemplate,
            Map<String, String> defaultVariables,
            List<String> guardrailInstructions,
            String promptVersion) {
        this.systemPromptTemplate = systemPromptTemplate;
        this.developerPromptTemplate = developerPromptTemplate;
        this.defaultVariables = defaultVariables;
        this.guardrailInstructions = guardrailInstructions;
        this.promptVersion = promptVersion;
    }

    public String getSystemPromptTemplate() {
        return systemPromptTemplate;
    }

    public String getDeveloperPromptTemplate() {
        return developerPromptTemplate;
    }

    public Map<String, String> getDefaultVariables() {
        return defaultVariables;
    }

    public List<String> getGuardrailInstructions() {
        return guardrailInstructions;
    }

    public String getPromptVersion() {
        return promptVersion;
    }
}
