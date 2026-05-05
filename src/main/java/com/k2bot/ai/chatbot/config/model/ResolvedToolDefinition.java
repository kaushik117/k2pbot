package com.k2bot.ai.chatbot.config.model;

import com.k2bot.ai.chatbot.persistence.entity.ToolType;

public final class ResolvedToolDefinition {

    private final String toolName;
    private final ToolType toolType;
    private final boolean requiresApproval;
    private final Integer timeoutMs;

    public ResolvedToolDefinition(
            String toolName,
            ToolType toolType,
            boolean requiresApproval,
            Integer timeoutMs) {
        this.toolName = toolName;
        this.toolType = toolType;
        this.requiresApproval = requiresApproval;
        this.timeoutMs = timeoutMs;
    }

    public String getToolName() {
        return toolName;
    }

    public ToolType getToolType() {
        return toolType;
    }

    public boolean isRequiresApproval() {
        return requiresApproval;
    }

    public Integer getTimeoutMs() {
        return timeoutMs;
    }
}
