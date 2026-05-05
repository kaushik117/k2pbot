package com.k2bot.ai.chatbot.prompt.model;

import java.util.Map;

public class PromptAssemblyResult {

    private final String systemPrompt;
    private final String developerPrompt;
    private final String userPrompt;
    private final Map<String, Object> variables;
    private final String promptVersion;
    private final PromptRenderMetadata metadata;

    public PromptAssemblyResult(
            String systemPrompt,
            String developerPrompt,
            String userPrompt,
            Map<String, Object> variables,
            String promptVersion,
            PromptRenderMetadata metadata) {
        this.systemPrompt = systemPrompt;
        this.developerPrompt = developerPrompt;
        this.userPrompt = userPrompt;
        this.variables = variables;
        this.promptVersion = promptVersion;
        this.metadata = metadata;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public String getDeveloperPrompt() {
        return developerPrompt;
    }

    public String getUserPrompt() {
        return userPrompt;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public String getPromptVersion() {
        return promptVersion;
    }

    public PromptRenderMetadata getMetadata() {
        return metadata;
    }
}
