package com.k2bot.ai.chatbot.config.model;

import java.time.Instant;

public final class ResolvedAssistantConfig {

    private final String assistantCode;
    private final String tenantId;
    private final String assistantName;
    private final boolean active;
    private final ResolvedPromptConfig promptConfig;
    private final ResolvedModelRoutingConfig modelRoutingConfig;
    private final ResolvedRagConfig ragConfig;
    private final ResolvedMemoryConfig memoryConfig;
    private final ResolvedToolConfig toolConfig;
    private final ResolvedSafetyConfig safetyConfig;
    private final ResolvedResponseConfig responseConfig;
    private final String configVersion;
    private final Instant resolvedAt;

    public ResolvedAssistantConfig(
            String assistantCode,
            String tenantId,
            String assistantName,
            boolean active,
            ResolvedPromptConfig promptConfig,
            ResolvedModelRoutingConfig modelRoutingConfig,
            ResolvedRagConfig ragConfig,
            ResolvedMemoryConfig memoryConfig,
            ResolvedToolConfig toolConfig,
            ResolvedSafetyConfig safetyConfig,
            ResolvedResponseConfig responseConfig,
            String configVersion,
            Instant resolvedAt) {
        this.assistantCode = assistantCode;
        this.tenantId = tenantId;
        this.assistantName = assistantName;
        this.active = active;
        this.promptConfig = promptConfig;
        this.modelRoutingConfig = modelRoutingConfig;
        this.ragConfig = ragConfig;
        this.memoryConfig = memoryConfig;
        this.toolConfig = toolConfig;
        this.safetyConfig = safetyConfig;
        this.responseConfig = responseConfig;
        this.configVersion = configVersion;
        this.resolvedAt = resolvedAt;
    }

    public String getAssistantCode() {
        return assistantCode;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getAssistantName() {
        return assistantName;
    }

    public boolean isActive() {
        return active;
    }

    public ResolvedPromptConfig getPromptConfig() {
        return promptConfig;
    }

    public ResolvedModelRoutingConfig getModelRoutingConfig() {
        return modelRoutingConfig;
    }

    public ResolvedRagConfig getRagConfig() {
        return ragConfig;
    }

    public ResolvedMemoryConfig getMemoryConfig() {
        return memoryConfig;
    }

    public ResolvedToolConfig getToolConfig() {
        return toolConfig;
    }

    public ResolvedSafetyConfig getSafetyConfig() {
        return safetyConfig;
    }

    public ResolvedResponseConfig getResponseConfig() {
        return responseConfig;
    }

    public String getConfigVersion() {
        return configVersion;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }
}
