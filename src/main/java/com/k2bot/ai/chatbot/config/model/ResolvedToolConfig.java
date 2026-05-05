package com.k2bot.ai.chatbot.config.model;

import java.util.List;

public final class ResolvedToolConfig {

    private final boolean enabled;
    private final List<ResolvedToolDefinition> allowedTools;
    private final boolean allowRuntimeSubsetSelection;
    private final Integer maxToolCallsPerRequest;
    private final Integer toolTimeoutMs;

    public ResolvedToolConfig(
            boolean enabled,
            List<ResolvedToolDefinition> allowedTools,
            boolean allowRuntimeSubsetSelection,
            Integer maxToolCallsPerRequest,
            Integer toolTimeoutMs) {
        this.enabled = enabled;
        this.allowedTools = allowedTools;
        this.allowRuntimeSubsetSelection = allowRuntimeSubsetSelection;
        this.maxToolCallsPerRequest = maxToolCallsPerRequest;
        this.toolTimeoutMs = toolTimeoutMs;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<ResolvedToolDefinition> getAllowedTools() {
        return allowedTools;
    }

    public boolean isAllowRuntimeSubsetSelection() {
        return allowRuntimeSubsetSelection;
    }

    public Integer getMaxToolCallsPerRequest() {
        return maxToolCallsPerRequest;
    }

    public Integer getToolTimeoutMs() {
        return toolTimeoutMs;
    }
}
